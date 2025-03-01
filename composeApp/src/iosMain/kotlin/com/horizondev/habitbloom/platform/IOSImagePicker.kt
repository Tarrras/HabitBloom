package com.horizondev.habitbloom.platform

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.Foundation.NSData
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSUUID
import platform.Foundation.writeToFile
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerEditedImage
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.UIKit.UIViewController
import platform.darwin.NSObject

class IOSImagePicker : ImagePicker {
    private val _imagePickerResult = MutableStateFlow<ImagePickerResult>(ImagePickerResult.None)
    override val imagePickerResult: StateFlow<ImagePickerResult> = _imagePickerResult.asStateFlow()

    private var currentViewController: UIViewController? = null
    private val imagePickerController = UIImagePickerController().apply {
        sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
    }
    private val delegate = ImagePickerDelegate(_imagePickerResult)

    fun attach(viewController: UIViewController) {
        currentViewController = viewController
        imagePickerController.delegate = delegate
    }

    override suspend fun pickImage() {
        currentViewController?.presentViewController(
            imagePickerController,
            animated = true,
            completion = null
        ) ?: run {
            _imagePickerResult.value = ImagePickerResult.Error("View Controller not attached")
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private class ImagePickerDelegate(
    private val result: MutableStateFlow<ImagePickerResult>
) : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {

    override fun imagePickerController(
        picker: UIImagePickerController,
        didFinishPickingMediaWithInfo: Map<Any?, *>
    ) {
        result.value = ImagePickerResult.Loading

        val image = (didFinishPickingMediaWithInfo[UIImagePickerControllerEditedImage]
            ?: didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage]) as? UIImage

        if (image == null) {
            result.value = ImagePickerResult.Error("Failed to get image")
            picker.dismissViewControllerAnimated(true, null)
            return
        }

        // Convert UIImage to JPEG data with 0.8 quality
        val imageData = UIImageJPEGRepresentation(image, 0.8)
        if (imageData == null) {
            result.value = ImagePickerResult.Error("Failed to get image data")
            picker.dismissViewControllerAnimated(true, null)
            return
        }

        val fileName = "${NSUUID().UUIDString}.jpg"
        val tempDirectory = NSTemporaryDirectory()
        val filePath = "$tempDirectory$fileName"

        val success = imageData.writeToFile(filePath, atomically = true)
        if (!success) {
            result.value = ImagePickerResult.Error("Failed to save image")
            picker.dismissViewControllerAnimated(true, null)
            return
        }

        result.value = ImagePickerResult.Success(filePath)
        picker.dismissViewControllerAnimated(true, null)
    }

    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        result.value = ImagePickerResult.Error("Image selection cancelled")
        picker.dismissViewControllerAnimated(true, null)
    }
}