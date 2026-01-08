package com.gerwalex.library.composables


import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.State.RESUMED
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope


/**
 * A composable function that executes a suspend [action] within a coroutine,
 * while ensuring it only runs when the lifecycle of the current
 * [LocalLifecycleOwner] is at least in the provided [state].
 *
 * This function utilizes [Lifecycle.repeatOnLifecycle] to handle the lifecycle
 * awareness, making it suitable for operations that should be active only
 * during specific lifecycle states (e.g., data collection when the UI is visible).
 *
 * The [action] will be launched as a new coroutine within a [LaunchedEffect],
 * and will be automatically cancelled when the composable leaves the composition or
 * when the lifecycle moves to a state lower than the provided [state]. The coroutine will be
 * restarted when the lifecycle moves back to the provided [state].
 *
 * @param state The minimum [Lifecycle.State] required for the [action] to be active.
 *              Defaults to [Lifecycle.State.RESUMED].
 * @param action The suspend function to be executed when the lifecycle is in the specified [state].
 *               This function receives a [CoroutineScope] as its receiver.
 *
 * Example usage:
 *
 * ```kotlin
 * @Composable
 * fun MyScreen(viewModel: MyViewModel = viewModel()) {
 *     RepeatOnLifecycleEffect(state = Lifecycle.State.STARTED) {
 *         viewModel.uiState.collect { state ->
 *             // Update UI based on state
 *             println("Ui state collected $state")
 *         }
 *     }
 *     //Rest of the composable
 * }
 * ```
 *
 * In this example, the `viewModel.uiState.collect` block will only be executed
 * when the lifecycle is at least in the [Lifecycle.State.STARTED] state.
 */
@Composable
fun RepeatOnLifecycleEffect(
    state: Lifecycle.State = RESUMED,
    action: suspend CoroutineScope.() -> Unit,
) {
    val lifecycle = LocalLifecycleOwner.current

    LaunchedEffect(key1 = Unit) {
        lifecycle.repeatOnLifecycle(state, block = action)
    }
}

/**
 * Composable function that observes the lifecycle events of a given [LifecycleOwner].
 *
 * This composable allows you to react to lifecycle events (such as ON_CREATE, ON_START, ON_RESUME, etc.)
 * within your composable functions. It uses [DisposableEffect] to properly add and remove a [LifecycleEventObserver]
 * to ensure that the observer is only active when the composable is in the composition.
 *
 * @param lifeCycleOwner The [LifecycleOwner] whose lifecycle events should be observed. Defaults to [LocalLifecycleOwner.current].
 *                       This parameter specifies the component (e.g., Activity, Fragment) whose lifecycle you want to monitor.
 * @param onEvent A lambda function that will be invoked whenever a lifecycle event occurs.
 *                - It receives the [LifecycleOwner] that emitted the event.
 *                - It receives the [Lifecycle.Event] that occurred.
 *
 * **Usage:**
 *
 * ```kotlin
 * @Composable
 * fun MyComposable() {
 *     ComposableLifecycle { owner, event ->
 *         when (event) {
 *             Lifecycle.Event.ON_CREATE -> {
 *                 println("MyComposable: ON_CREATE")
 *             }
 *             Lifecycle.Event.ON_START -> {
 *                 println("MyComposable: ON_START")
 *             }
 *             Lifecycle.Event.ON_RESUME -> {
 *                 println("MyComposable: ON_RESUME")
 *             }
 *             Lifecycle.Event.ON_PAUSE -> {
 *                 println("MyComposable: ON_PAUSE")
 *             }
 *              Lifecycle.Event.ON_STOP -> {
 *                 println("MyComposable: ON_STOP")
 *             }
 *             Lifecycle.Event.ON_DESTROY -> {
 *                 println("MyComposable: ON_DESTROY")
 *             }
 *             Lifecycle.Event.ON_ANY -> {
 */
@Composable
fun ComposableLifecycle(
    lifeCycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onEvent: (LifecycleOwner, Lifecycle.Event) -> Unit
) {
    DisposableEffect(lifeCycleOwner) {
        val observer = LifecycleEventObserver { source, event ->
            onEvent(source, event)
        }
        lifeCycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(observer)
        }
    }
}