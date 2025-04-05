# Test Application
This is first and foremost a test bed for the purpose of ensuring that the `Tendril` features and capabilities work and function as expected. To better reflect a real-work application, it is divided into several libraries with a single "main" acting an the central launch point. The capabilities of the application are irrelevant. It can be launched in one of two ways:
1. As an application via `test-app-main.tempApp.AppMain` (this contains a `main()`)
2. As a JUnit test via `test-app-main`

Both will ultimately do the same thing, with the main difference being that the JUnit approach allows for integration with automated test suites and automatic verification via pipelines.

Though the specifics of what the application is attempting to achieve are meaningless, it can be used as a reference for how to build a meaningful `Tendril` application.
