package com.horizondev.habitbloom.screens.habits.domain

class ActiveHabitAlreadyExistsException(habitId: String) :
    IllegalStateException("Active habit already exists for habitId=$habitId")
