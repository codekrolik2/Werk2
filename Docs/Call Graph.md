This Markdown document was created with https://stackedit.io. If the viewer of your choice doesn't support features like [mermaid diagrams](https://mermaid-js.github.io/mermaid/#/), please open it on [StackEdit](https://stackedit.io).
# Call graph
The following example shows calls happening as a result of direct invocation of Flow1.

Legend:
```mermaid
flowchart LR
ProjectedListenerCall -.-o ProjectedListenerCall_
OwnListenerCall --o OwnListenerCall_
CallWithProjection ==> CallWithProjection_
CallInNewProjectionContext ==x CallInNewProjectionContext_
```
N.B.: A Transition function can be called twice in different contexts from the same step, once as Exec and another time as Transit. The two calls would project two different sets of Listeners, and name clash should be avoided in this case.
Possible remedy would be adding a serial, as in `Step.Transit_1`, `Step.Transit_2`.
Also, when Transitioner is used as Executor, both projected Executor events listeners will trigger, as well as Transitioner's own listeners. This is due to the fact that in logical sense the Transitioner will be invoked as an anonymous Flow. 
```
1.     Executor EXECUTOR_STARTED { PROJECTED_ES }
2.       Anonymous FLOW_STARTED { }
3.         Anonymous STEP_STARTED { }
4.           Transitioner TRANSITIONER_STARTED { TS }
5.             ...
6.           Transitioner TRANSITIONER_FINISHED { TF }
7.         Anonymous STEP_FINISHED { }
8.       Anonymous FLOW_FINISHED { }
9.     Executor EXECUTOR_FINISHED { PROJECTED_EF }
```
 

Unexpected Q & A: 
Q1: New projection context vs instantiation by using Engine features from code - former doesn't inherit engine listeners, latter does.
A1: New projection context can't inherit Engine Listeners for a simple reason that Engine listeners would highly likely create loops with called functions.
Example:
```mermaid
classDiagram
class Engine
class flowStart
Engine : FLOW_STARTED flowStart
Engine --> flowStart
```
The difference in flow present itself as follows:
1) Without Engine context inheritance:
```mermaid
flowchart LR
Engine ==> Engine.flowStart
Engine.flowStart -.-> FLOW_STARTED:flowStart
```
2) With Engine context inheritance:
```mermaid
flowchart LR
Engine ==> Engine.flowStart
Engine.flowStart -.-> FLOW_STARTED:flowStart
FLOW_STARTED:flowStart -.-> FLOW_STARTED:flowStart
```
Since we're obligated to test all possible call indirections to be acyclic, modeling the invocation of all existing functions as Flows would definitely be a part of our check. The example above shows calling of raw function `flowStart` as a standalone Flow. Thus, inheriting Engine context in new projection contexts (like Listener Call) could potentially create call indirection loop situations when no simple remediation by restructuring is possible.
P.S. also, due to "everything is a Flow" semantics, if we indirect it to, say, Exec - in case engine's listeners are projected, not just Exec bindings will trigger. In fact, engine's Flow, Step, Exec and Transit binding will all trigger due to the fact that Exec will be run in the context of anonymous Flow, anonymous Step with anonymous "#exit" Transit attached. I see that as somewhat unexpected, counterintuitive and rarely desirable.

Q2: Providing a custom context in `CallInNewProjectionContext` use case? 
A2: There is no reason for introducing this additional layer of complexity. The way it looks right now - things are flexible enough the way they are. 

Transitioner & ExecBlock - all calls start in new context
ListenerBlock - all calls start in new context

```mermaid
flowchart LR
Engine ==> Engine.Flow1
Engine.Flow1 -.-o FS_E
Engine.Flow1 -.-o FF_E
Engine.Flow1 --o FS_F1
Engine.Flow1 --o FF_F1
Engine.Flow1 ==> Engine.Flow1.Step1
Engine.Flow1.Step1 -.-o SS_E
Engine.Flow1.Step1 -.-o SF_S1
Engine.Flow1.Step1 -.-o SF_F1
Engine.Flow1.Step1 -.-o SF_E
Engine.Flow1.Step1 --o SS_S1
Engine.Flow1.Step1 --o SS_F1
Engine.Flow1.Step1 ==> Engine.Flow1.Step1.Executor1
Engine.Flow1.Step1 ==> Engine.Flow1.Step1.Transitioner1
Engine.Flow1.Step1.Executor1 -.-o ES_E
Engine.Flow1.Step1.Executor1 -.-o ES_S1
Engine.Flow1.Step1.Executor1 -.-o ES_F1
Engine.Flow1.Step1.Executor1 -.-o EF_E
Engine.Flow1.Step1.Executor1 -.-o EF_S1
Engine.Flow1.Step1.Executor1 -.-o EF_F1
Engine.Flow1.Step1.Executor1 --o ES_E1
Engine.Flow1.Step1.Executor1 --o EF_E1
Engine.Flow1.Step1.Executor1 ==x Flow2
Engine.Flow1.Step1.Transitioner1 -.-o TS_E
Engine.Flow1.Step1.Transitioner1 -.-o TF_E
Engine.Flow1.Step1.Transitioner1 -.-o TS_F1
Engine.Flow1.Step1.Transitioner1 -.-o TF_F1
Engine.Flow1.Step1.Transitioner1 -.-o TS_S1
Engine.Flow1.Step1.Transitioner1 -.-o TF_S1
Engine.Flow1.Step1.Transitioner1 --o TS_T1
Engine.Flow1.Step1.Transitioner1 --o TF_T1
Engine.Flow1.Step1.Transitioner1 ==x Step2

Flow2 --o FS_F2
Flow2 --o FF_F2

Flow2 ==> Flow2.Step2
Flow2.Step2 -.-o SS_F2
Flow2.Step2 -.-o SF_F2
Flow2.Step2 --o SS_S2
Flow2.Step2 --o SF_S2
Flow2.Step2 ==> Flow2.Step2.Executor2
Flow2.Step2 ==> Flow2.Step2.Transitioner2
Flow2.Step2.Executor2 -.-o ES_F2
Flow2.Step2.Executor2 -.-o EF_F2
Flow2.Step2.Executor2 -.-o ES_S2
Flow2.Step2.Executor2 -.-o EF_S2
Flow2.Step2.Executor2 --o ES_E2
Flow2.Step2.Executor2 --o EF_E2
Flow2.Step2.Executor2 ==x ExecFunction3
Flow2.Step2.Transitioner2 -.-o TS_F2
Flow2.Step2.Transitioner2 -.-o TF_F2
Flow2.Step2.Transitioner2 -.-o TS_S2
Flow2.Step2.Transitioner2 -.-o TF_S2
Flow2.Step2.Transitioner2 --o TS_T2
Flow2.Step2.Transitioner2 --o TF_T2
Flow2.Step2.Transitioner2 ==x TransitFunction4


FF_F2 ==x ExecFunction2
FF_F2 ==x Transitioner2
FF_F2 ==x Step2
FF_F2 ==x Flow3
Transitioner2 --o TS_T2
Transitioner2 --o TF_T2
Transitioner2 ==x TransitFunction4
Step2 --o SS_S2
Step2 --o SF_S2
Step2 ==> Step2.Executor2
Step2 ==> Step2.Transitioner2
Step2.Executor2 -.-o ES_S2
Step2.Executor2 -.-o EF_S2
Step2.Executor2 --o ES_E2
Step2.Executor2 --o EF_E2
Step2.Executor2 ==x ExecFunction3
Step2.Transitioner2 -.-o TS_S2
Step2.Transitioner2 -.-o TF_S2
Step2.Transitioner2 --o TS_T2
Step2.Transitioner2 --o TF_T2
Step2.Transitioner2 ==x TransitFunction4
Flow3 --o FS_F3
Flow3 --o FF_F3
Flow3 ==> Flow3.Step2

Flow3.Step2 -.-o SS_F3
Flow3.Step2 -.-o SF_F3
Flow3.Step2 --o SS_S2
Flow3.Step2 --o SF_S2
Flow3.Step2 ==> Flow3.Step2.Executor2
Flow3.Step2 ==> Flow3.Step2.Transitioner2
Flow3.Step2.Executor2 -.-o ES_F3
Flow3.Step2.Executor2 -.-o EF_F3
Flow3.Step2.Executor2 -.-o ES_S2
Flow3.Step2.Executor2 -.-o EF_S2
Flow3.Step2.Executor2 --o ES_E2
Flow3.Step2.Executor2 --o EF_E2
Flow3.Step2.Executor2 ==x ExecFunction3
Flow3.Step2.Transitioner2 -.-o TS_F3
Flow3.Step2.Transitioner2 -.-o TF_F3
Flow3.Step2.Transitioner2 -.-o TS_S2
Flow3.Step2.Transitioner2 -.-o TF_S2
Flow3.Step2.Transitioner2 --o TS_T2
Flow3.Step2.Transitioner2 --o TF_T2
Flow3.Step2.Transitioner2 ==x TransitFunction4

Flow3 ==> Flow3.Transitioner3
Flow3.Transitioner3 -.-o SS_F3
Flow3.Transitioner3 -.-o SF_F3
Flow3.Transitioner3 -.-o TS_F3
Flow3.Transitioner3 -.-o TF_F3
Flow3.Transitioner3 --o TS_T3
Flow3.Transitioner3 --o TF_T3
Flow3.Transitioner3 ==x TransitFunction1

Engine.Flow1 ==> Step4
Step4 --o SS_S4
Step4 --o SF_S4
Step4 ==> Executor3
Step4 ==> Step4.TransitFunction4
Executor3 --o ES_E3
Executor3 --o EF_E3
Executor3 ==x ExecFunction5
Step4.TransitFunction4 --o TS_S4
Step4.TransitFunction4 --o TF_S4
Step4.TransitFunction4 ==x TransitFunction4
```
