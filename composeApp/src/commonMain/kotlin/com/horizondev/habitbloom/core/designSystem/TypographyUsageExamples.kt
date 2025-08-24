package com.horizondev.habitbloom.core.designSystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Comprehensive examples of how to use the enhanced BloomTheme typography system.
 *
 * The typography system now includes 24 different text styles organized into
 * semantic categories for consistent and professional text hierarchy.
 */

// MARK: - Display Typography Examples
@Composable
fun DisplayTypographyExample() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Display Large",
            style = BloomTheme.typography.displayLarge,
            color = BloomTheme.colors.textColor.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Display Medium",
            style = BloomTheme.typography.displayMedium,
            color = BloomTheme.colors.textColor.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Display Small",
            style = BloomTheme.typography.displaySmall,
            color = BloomTheme.colors.textColor.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// MARK: - Heading Hierarchy Examples
@Composable
fun HeadingHierarchyExample() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Heading 1 - Main Page Title",
            style = BloomTheme.typography.h1,
            color = BloomTheme.colors.textColor.primary
        )

        Text(
            text = "Heading 2 - Section Title",
            style = BloomTheme.typography.h2,
            color = BloomTheme.colors.textColor.primary
        )

        Text(
            text = "Heading 3 - Subsection Title",
            style = BloomTheme.typography.h3,
            color = BloomTheme.colors.textColor.primary
        )

        Text(
            text = "Heading 4 - Card Title",
            style = BloomTheme.typography.h4,
            color = BloomTheme.colors.textColor.primary
        )

        Text(
            text = "Heading 5 - Component Title",
            style = BloomTheme.typography.h5,
            color = BloomTheme.colors.textColor.primary
        )

        Text(
            text = "Heading 6 - Small Title",
            style = BloomTheme.typography.h6,
            color = BloomTheme.colors.textColor.primary
        )
    }
}

// MARK: - Body Text Examples
@Composable
fun BodyTextExample() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Body Large - This is large body text used for important content that needs to stand out while still being readable in paragraphs.",
            style = BloomTheme.typography.bodyLarge,
            color = BloomTheme.colors.textColor.primary
        )

        Text(
            text = "Body Medium - This is the standard body text used for most content. It's optimized for readability and is the most common text size.",
            style = BloomTheme.typography.bodyMedium,
            color = BloomTheme.colors.textColor.primary
        )

        Text(
            text = "Body Small - This is smaller body text used for secondary content, fine print, or when space is limited but readability is still important.",
            style = BloomTheme.typography.bodySmall,
            color = BloomTheme.colors.textColor.secondary
        )
    }
}

// MARK: - Label and UI Text Examples
@Composable
fun LabelAndUITextExample() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Label Large - Form Field Label",
            style = BloomTheme.typography.labelLarge,
            color = BloomTheme.colors.textColor.primary
        )

        Text(
            text = "Label Medium - Button Text",
            style = BloomTheme.typography.labelMedium,
            color = BloomTheme.colors.textColor.primary
        )

        Text(
            text = "LABEL SMALL - TAB TEXT",
            style = BloomTheme.typography.labelSmall,
            color = BloomTheme.colors.textColor.secondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Caption text for images, tooltips, or additional information",
            style = BloomTheme.typography.caption,
            color = BloomTheme.colors.textColor.secondary
        )

        Text(
            text = "OVERLINE TEXT FOR CATEGORIES",
            style = BloomTheme.typography.overline,
            color = BloomTheme.colors.textColor.secondary
        )
    }
}

// MARK: - Button Typography Examples
@Composable
fun ButtonTypographyExample() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Large Button Text",
            style = BloomTheme.typography.buttonLarge,
            color = BloomTheme.colors.primary
        )

        Text(
            text = "Medium Button Text",
            style = BloomTheme.typography.buttonMedium,
            color = BloomTheme.colors.primary
        )

        Text(
            text = "Small Button Text",
            style = BloomTheme.typography.buttonSmall,
            color = BloomTheme.colors.primary
        )
    }
}

/**
 * TYPOGRAPHY USAGE GUIDELINES:
 *
 * 1. **Display Styles** - Use for hero content and splash screens
 *    - displayLarge: App launch screens, hero banners
 *    - displayMedium: Welcome messages, feature highlights
 *    - displaySmall: Section headers in large screens
 *
 * 2. **Heading Hierarchy** - Create clear content structure
 *    - h1: Page titles, main headings
 *    - h2: Section headings
 *    - h3: Subsection headings
 *    - h4: Card titles, dialog titles
 *    - h5: Component titles, list item titles
 *    - h6: Small titles, metadata headings
 *
 * 3. **Body Text** - For readable content
 *    - bodyLarge: Feature descriptions, important content
 *    - bodyMedium: Standard paragraphs, most common text
 *    - bodySmall: Secondary content, helper text
 *
 * 4. **Labels** - For UI components
 *    - labelLarge: Form labels, important UI text
 *    - labelMedium: Button text, navigation items
 *    - labelSmall: Tab labels, small UI elements
 *
 * 5. **Utility Text** - For special purposes
 *    - caption: Image captions, tooltips, footnotes
 *    - overline: Categories, tags, section identifiers (usually uppercase)
 *
 * 6. **Button Text** - For interactive elements
 *    - buttonLarge: Primary action buttons
 *    - buttonMedium: Secondary action buttons
 *    - buttonSmall: Tertiary actions, compact buttons
 *
 * 7. **Legacy Compatibility** - Still available
 *    - title: Maps to h3 (24sp)
 *    - heading: Maps to h4 (20sp)
 *    - subheading: Maps to h6 (16sp)
 *    - body: Maps to bodyMedium (14sp)
 *    - small: Maps to bodySmall (12sp)
 *    - button: Maps to buttonMedium (14sp)
 *    - formLabel: Maps to labelLarge (14sp)
 *
 * COLOR PAIRING RECOMMENDATIONS:
 * - Primary content: BloomTheme.colors.textColor.primary
 * - Secondary content: BloomTheme.colors.textColor.secondary
 * - Disabled content: BloomTheme.colors.textColor.disabled
 * - Accent content: BloomTheme.colors.textColor.accent
 * - On colored backgrounds: BloomTheme.colors.primaryForeground, etc.
 *
 * All typography styles use the Poppins font family with optimized
 * line heights and letter spacing for maximum readability!
 */
