This Markdown document was created with https://stackedit.io. If the viewer of your choice doesn't support features like [mermaid diagrams](https://mermaid-js.github.io/mermaid/#/), please open it on [StackEdit](https://stackedit.io).
# Werk2

Werk2 is a continuation of development of ideas introduced in the original [Werk](https://github.com/codekrolik/Werk) project, redesigned for a greater scope of providing a general-purpose control flow structuring framework sharing features of [Procedural programming](https://en.wikipedia.org/wiki/Procedural_programming) with elements of [Dataflow programming](https://en.wikipedia.org/wiki/Dataflow_programming). This framework is designed as a layer on top of a general-purpose language and is not restrictive in terms of full and complete usage of features of underlying language. 
It's important to keep in mind, however, that an efficient implementation would highly likely run on top of [Reactor](https://en.wikipedia.org/wiki/Reactor_pattern), and typical best practices would apply. E.g. [The Golden Rule - Don’t Block the Event Loop](https://vertx.io/docs/vertx-core/java/#golden_rule).
This repository holds implementation of Werk2 for Java/JVM and runs on top of [Vert.x Multi-Reactor](https://vertx.io/docs/vertx-core/java/#_reactor_and_multi_reactor) implementation.

