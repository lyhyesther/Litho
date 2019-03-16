## Litho

Litho 使用Yoga 来布局,而Yoga会使用到本地依赖， 所以需要在应用初始化的时候，初始化Soloader.

### 1、LithoView
LithoView 是一个Android 的ViewGroup.它是Litho 组件和Android View 之间的桥梁，负责渲染组件

### 2、创建组件
组件Spec 会生成在UI 中实际使用的组件
组件Spec有两种类型：
Layout Spec :
可以包含其他组件的layout , 类似于Android 中的ViewGroup;
Mount Spec:
组件用来渲染一个view或者drawable

layout spec的整个结构：
@LayoutSpec
class MyComponentSpec {

  @OnCreateLayout
  static Component onCreateLayout(
      ComponentContext c,
      @Prop String title,
      @Prop Uri imageUri) {
    ...
  }
}

注意：
组件Spec 只是一个具有一些注解的原始java类，
组件Spec 是无状态的，没有任何的类成员
使用@Prop注释的属性 会自动变成组件的Builder的一部分

由于组件 会根据 组件Spec 生成，所以必须添加注解处理器到Gradle文件中。

Layout Spec
在layout spec 中，只需要使用@OnCreateLayout注解，它会生成一个 ComponentLayout 对象树
@OnCreateLayout 注解的方法第一个参数必须是ComponentContext , 其余的属性使用@Prop注解

Mount Spec 
Mount Spec仅当 需要集成自己的View或Drawable到Litho中时才会被创建。 Mount 操作是指布局树中的所有组件都会执行来确定显示的是View还是Drawable.
Mount Spec 类需要使用@MoutSpec注解，并且至少实现一个@OnCreateMountContent注解的方法。下面列出的其他方法是可选的：
@OnPrepare 在布局计算之前运行
@OnMeasure 在布局期间运行，是一个可选操作，如果Yoga已经确定了布局的边界，就不会调用该方法，比如组件设置了静态的宽度和高度值
@OnBoundsDefined
@OnCreateMountContent 组件依附到宿主View之前调用，该Content 可能被组件中的其他实例重用。
@OnMount 组件依附到宿主View之前调用
@OnBind 组件依附到宿主View之后调用
@OnUnbind 组件脱离宿主View之前调用
@OnUnmount 组件脱离宿主View之后调用，可选

挂载过程Mounting
挂载过程和Android RecyclerView的Adapter非常相似。如果回收池是空的，onCreateMountContent 【创建挂载的内容】方法就会创建或者初始化View/Drawable
onCreateMountContent 的返回类型应该总是匹配onMount的第二个参数类型。它必须是View 或者 Drawable的子类
onCreateMountContent 不接受@Prop注解的参数，或是其他注解的参数
@OnMount 方法总是运行在主线程，所以在其中避免执行耗时操作

输入和输出分阶段
可以将耗时操作分离到@OnPrepare方法中，布局计算之前它只会执行一次，并且是在后台线程中执行。

Spec LifeCycle 和组件类

一个组件Spec类会被处理生成一个继承自ComponenntLifecycle的同名组件类，只不过少了后缀Spec,生成的类就是在产品中使用的类。在运行时，Spec类会作为生成代码类的一个代理。
生成类暴露的唯一API是create(...)方法，该方法返回一个相应的Builder类，Builder类包含了由@Prop注解的那些属性

在运行时，一个类型的所有组件实例，共享相同的ComponentLifecycle引用。也就是说，一个类型的所有组件实例只会有一个Spec实例，而不是一个组件实例对应一个Spec实例。

直接使用 LithoView  的view体系结构，会在UI主线程中同步绘制。如果需要在非主线程中布局，可以使用异步布局。

