This Markdown document was created with https://stackedit.io. If the viewer of your choice doesn't support features like [mermaid diagrams](https://mermaid-js.github.io/mermaid/#/), please open it on [StackEdit](https://stackedit.io).
# Werk2 - notes on Type Safety (java)
Parameter type comparisons happen for two reasons during configuration validation
1. Make sure no ambiguity exists in function signatures declaration;
2. Make sure all declared calls are type-safe as far as it can be can determined.

The way Type checks work in those two cases is a little bit different, but first of all let's establish common grounds for Parameter Types in Werk2.
`FunctionParameter` is defined as follows (trimmed):
```
interface FunctionParameter {
	String getName();
	ParameterDirection getDirection();
	WerkParameterType getType();
	Optional<String> getRuntimeType();
	Optional<ParameterPassing> getPassing();
}
```
In this structure Name is pretty self-explanatory, Direction can be IN or OUT, and since there is no way to manually set BY_REF or BY_VAL Passing in Java, it's always SYSTEM_DEFAULT.
Thus, the actual typization boils down to `WerkParameterType Type` and `Optional<String> RuntimeType`.

Parameters in Werk2 are typed primarily as WerkParameterType (`Type`), and can have optional definition as a raw Java type (`RuntimeType`), with the exception of `RUNTIME`, for which `RuntimeType` must be specified. Werk2 supports simplified JSON-like type system for ease of interoperability and portability, like serialization and cross-context parameter passing.
Let's look at WerkParameterTypes and corresponding Java types.

Table 1: Werk and Java types
|WerkParameterType|Java types                                       |
|-----------------|-------------------------------------------------|
|**Serializeable:**|                                                |
|LONG             | int, Integer, long, Long                        |
|CHAR             | char, Character                                 |
|DOUBLE           | double, Double                                  |
|BOOL             | boolean, Boolean                                |
|STRING           | String                                          |
|BYTES            | byte[]                                          |
|LIST             | ? implements List &lt;of serializable types&gt; |
|MAP              | ? implements Map &lt;of serializable types&gt;  |
|**Non-serializeable:**|                                            |
|RUNTIME          | any Java type (RuntimeType must be specified)   |
|**System:**      |                                                 |
|STEP             | special type, constant declaration only         |
|FUNCTION         | special type, constant declaration only         |

Matching system types is trivial, so we'll concentrate on non-system types here.
As it follows from the table above, it's either RUNTIME or serializeable. However, it's not illegal to declare a parameter like `RUNTIME String`, which wont be serialized. Moreover, while Werk LIST and MAP parameters are expected to carry only Werk-Serializeable types, we can use broader range of Java Types in RUNTIME parameters, e.g. `RUNTIME List<InputStream>`. On top of that, Java's own type safety has nuances, for example `func(List<Long> lst)` and `func(ArrayList<Long> lst)` are considered different signatures, while both parameters will legitimately map to Werk LIST type.
Also, at runtime Java erases all generic type information and passes everything as raw types. Compile-time checks are intended to minimize type mismatch errors, but ATM Werk is using dynamic Reflection-based invocation by default. Even though Werk-to-Java compiler is clearly a feature that's on the roadmap (mainly to improve performance), dynamic invocation is not likely to disappear as a class. Due to lack of compile-time checks on Java level there can be a lot of type cast errors if Werk doesn't provide additional type safety control at initialization time, so we definitely don't want to just allow all types fly around unchecked and cause type mismatch errors that are preventable.
Since there are a lot of nuances in type handling it's very important to clearly design and describe a set of rules based upon which Werk will determine type safety, while not being overly complex and avoid causing pain in developers by introducing rules that are too restrictive.

1. Comparing signatures of overloaded functions
As it was mentioned above, it's possible to declare different signatures that are logically the same, due to various Java types that can map to the same Werk Type.
When comparing overloaded signatures, the question we're trying to answer is: "Are the signatures subject to comparison similar enough to produce ambiguity?", and this question is asked majorly from the perspective of Werk type system. To answer that, we need to understand what parts of signatures we actually need to compare. First of all (and so in line with Java language) we don't take into account function's return type. Similarly to that, we exclude OUT parameters from the comparison, because it's not mandatory to deal with their values in Werk call, and logically they can be viewed as an extension to function return type. Thus, we define our signature by its set of IN parameters.
As far as function declaration goes, there are substantial differences between Java and Werk. In Java the order of parameters matters, while in Werk the name of the parameter is important, but not its position. <br/>
Thus: `func(int prm1, char prm2)` and `func(int prm2, char prm1)` is the same signature in Java, but not in Werk.
However: `func(int prm1, char prm2)` and `func(char prm2, int prm1)` are different signatures in Java, but the same signature in Werk.<br/>
Moreover, a single Werk Type can be represented by different Java types (see Table 1).
Note that Java release build removes all information about parameter names, and Reflection would show them as arg0, arg1, arg2, etc. When dealing with Raw functions declarations in XML configuration it's important to remember that even though parameters are declared with names and in Werk calls are initialized by name rather than position, the matching of physical Java functions to logical Werk declarations is happening via Reflection and parameter names don't matter, while their types and position in Werk declaration are defining.
Also, in Annotation-based configuration it's advisable to duplicate parameter name in declaration, e.g.:
`@In(name="i") int i` Even though Werk will try its best to get information about annotated parameter name via Reflection, as it was said earlier, release build will delete this information and the name of the parameter in metadata can change from "i" to something like "arg0", and initialization of Werk function calls that refer to the parameter by name "i" will result in a configuration error.<br/>
The first step in finding duplicate overloaded signatures in Werk functions is to ensure that the number of IN parameters is the same and they have the same names. Next, we compare types of matching parameters.
When it comes to basic types (`LONG`, `CHAR`, `DOUBLE`, `BOOL`, `STRING`, `BYTES`), we simply assume a strong relationship between types as shown in Table1.
E.g.: all of the following types: `LONG`, `LONG int`, `LONG Integer`, `LONG long`, `LONG Long`, `RUNTIME int`, `RUNTIME Integer`, `RUNTIME long`, `RUNTIME Long`  will be considered representing Werk Type `LONG` and therefore identical. Other basic types will be analyzed similarly.<br/>
The logic is similar for `LIST` and `MAP`: since `LIST` and `MAP` are WerkTypes, we want to ensure that we exclude any ambiguity in signature definitions. Thus, all of the following types: `LIST`, `LIST ? implements List`, `RUNTIME ? impements List` will be considered representing Werk Type `LIST` and therefore identical, and similarly for `MAPs`.<br/>
So far we went through matching all serializeable parameter types with their `RUNTIME` counterparts and vice versa, and the last possibility left is `RUNTIME` parameters matching with other `RUNTIME` parameters. Rather straightforwardly, since all such parameters must have a RuntimeType field set, we will match them in the same way Java does - i.e. by base type, ignoring generic declarations.

2. Validating calls
Basic types will be matched the same way we match in 1, and automatic java-level casting will be applied when needed.
In case of `LIST` and `MAP`, we can consider their RuntimeTypes, if such are specified, otherwise we assign RuntimeTypes automatically, non-generic `java.util.List` to `LIST` and `java.util.Map` to `MAP`, and run our match based on those types using the same rules for comparison that would apply to `RUNTIME` parameters. To reiterate, specifying RuntimeType is mandatory for `RUNTIME` parameters.<br/>
The check to be performed can be decribed as follows:
*RuntimeTypes of caller and callee must satisfy the following condition: `caller's Type` is `? extends callee's Type`, for base types and recursively for all generic declarations.*
- Thus, a match for parameter types at call time would be, e.g. if `A` is a generic class and `B` extends `C`:
`C` -> `C`; (`C` is `C`)
`B` -> `C`; (`B` is a subclass of `C`, can be cast)
`A<C>` -> `A<C>`; (`C` is `C`)
`A<B>` -> `A<C>`; (`B` is a subclass of `C`, can be cast)
- However, the following call can't be allowed:
`X` -> `B`; (`X` cannot be cast to `B`)
`C` -> `B`; (`C` also cannot be cast to `B`, even though it's `B's` superclass)
`A<X>` -> `A<B>`; (`X` cannot be cast to `B`)
`A<C>` -> `A<B>`; (`C` cannot be cast to `B`)
This will cause configuration error on initialization.
- Also, potential type mismatches can occur in the following calls:
`A` -> `A`
`A` -> `A<C>`
`A<C>` -> `A`
and more intricately, 
`<A<A>>` -> `A<A<C>>`
`<A<A<C>>>` -> `A<A>`
etc.
In those examples, Due to lack of information about underlying generic types of `A`, we can't guarantee type safety. However, we can't guarantee the opposite as well, unlike in the previous entry. It may be too strict to throw a configuration error in such cases, but since this is a potential problem, displaying a warning would be a bare minimum action to be taken when encountering such declarations.
- Incompatibility between serializeable and non-serializeable may also manifest itself in generic declarations. For example, even if WerkType `LIST` doesn't show any structure, type incompatibility with e.g. `RUNTIME List<InputStream>` is obvious, since `InputStream` is not serializeable.

Serializeable Werk Types `LIST` and `MAP` assume that their elements are also of serializeable Types, as shown in Table 1. These elements can in their turn be `LISTs` or `MAPs` of serializeable Werk Types. However,  a `RuntimeType` can be specified for any Werk Type, not necessarily `RUNTIME`. WerkParameterTypes `LIST` and `MAP` by themselves do not carry any information about the structure of their elements, neither do they assume any rigid structure, i.e. those collections can hold elements of different types and sub-collections of different structures. But since Java types support generics, information about underlying structure can be included in `Optional<String> RuntimeType` field of `FunctionParameter`, which then will be type-checked at initialization time, as discussed above. 
An example of a following structure defined in such a way would be:
`MAP Map<String, List<Map<Integer, List<String>>>>`
Specifying the `RuntimeType` information can be utilized to ensure better type safety of function and call declarations. For example, declaring `LIST List<InputStream>` will result in a configuration error, because `InputStream` is not serializeable. One way to declare such parameter would be `RUNTIME List<InputStream>`. On the other hand, declaration `RUNTIME List<String>` is perfectly legitimate, as well as `LIST List<String>`.
