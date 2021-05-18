This Markdown document was created with https://stackedit.io. If the viewer of your choice doesn't support features like [mermaid diagrams](https://mermaid-js.github.io/mermaid/#/), please open it on [StackEdit](https://stackedit.io).
# Annotation-based configuration
Annotation-based configuration is intended to simplify definition of Raw functions by marking them as WerkFunctions directly in the code, as opposed to less concise and more error-prone XML config. It is also allows to avoid confusion when mapping Werk's parameter name-based signatures definition to Java's parameter position-based signatures.

It boils down to 3 annotations: `@WerkFunction` annotation that's applied to static methods and  `@In` and `@Out` annotations that are applied to parameters.
Apart from self-documentation entries, those annotations have the following fields:
- `@WerkFunction`
`name`: logical name of the function, if need to be different from its physical name.
Reasons for specifying such name is to simplify configuration (automatically it's going to be set to `package.class.method`), also some build options may result in renaming of classes on bytecode level, which also can be mitigated by specifying a logical function name.
- `@In` and `@Out`
`name` - overrides original parameter name. It's advisable to duplicate parameter name in declaration, e.g. `@In(name="i") int i`. Even though Werk will try its best to get information about annotated parameter name via Reflection, release build will likely delete this information and the name of the parameter in bytecode can change from "i" to something like "arg0", causing initialization of Werk function calls that refer to the parameter by the name "i" result in a configuration error.
`type` - explicitly specifies WerkType. By default set to AUTO, which means WerkType will be determined automatically.
Exact implementation can be found in the code: `WerkTypeMatcher.matchType(Type type)`.
 `runtimeType` - overrides parameter's runtimeType. By default is set to actual type of parameter (including generic declarations). Not recommended to set to anything that can cause type mismatch errors.

Since Java doesn't support `@Out` parameters, they are implemented through special type `org.werk2.common.OutParam<T>`. Any parameter marked as out parameter must be of `OutParam` type. In this way, if we need our function to supply an out parameter of type Integer, we declare a physical parameter `@Out OutParam<Integer> outInt`.

## WerkAnnotationScanner
Class `org.werk2.config.annotation.scan.WerkAnnotationScanner` finds all methods annotated with `@WerkFunction` and creates a corresponding Werk configuration (method `loadRawFunctions()`).
It performs check to ensure that all methods are static, their returns type are proper and all parameters are annotated. For parameters it also automatically determines Werk types, if not specified explicitly. Overloaded signatures are combined into `Function` structure and a list of Functions is returned to the caller.

Scanner doesn't try to retrieve or store Reflection structures like `Method`, just assembles configuration entries for Raw functions to be supplied to common configuration registry.
