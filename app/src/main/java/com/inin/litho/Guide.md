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

测量过程
如果想定义组件在布局计算期间组件如何被测量，需要实现@OnMeasure 注解的方法

应该更新
为了避免重复测量和挂载，Layout Spec 提供了@ShouldUpdate注解。 @ShouldUpdate 的调用取决于一个组件是否是一个纯渲染函数。一个纯渲染函数是指渲染的结果只依赖于它的属性和状态，
也就是说组件在@OnMount期间不应该访问任何全局的可变变量。

预分配
可以在回收池中预分配组件实例

Spec LifeCycle 和组件类

一个组件Spec类会被处理生成一个继承自ComponenntLifecycle的同名组件类，只不过少了后缀Spec,生成的类就是在产品中使用的类。在运行时，Spec类会作为生成代码类的一个代理。
生成类暴露的唯一API是create(...)方法，该方法返回一个相应的Builder类，Builder类包含了由@Prop注解的那些属性

在运行时，一个类型的所有组件实例，共享相同的ComponentLifecycle引用。也就是说，一个类型的所有组件实例只会有一个Spec实例，而不是一个组件实例对应一个Spec实例。

直接使用 LithoView  的view体系结构，会在UI主线程中同步绘制。如果需要在非主线程中布局，可以使用异步布局。

属性 @Prop
一个组件的给定属性在所有方法中使用统一注解
相同的属性可以在生命周期的多个方法中定义和访问
在LayoutSpec和MountSpec中的属性定义和使用方式是一样的

属性默认值
可以使用@PropDefault 注解在spec类中的静态变量中来实现属性默认值功能
 @PropDefault static final String prop1 = "mydefaultvalue";
 也支持从资源文件中读取资源
 @PropDefault(resType = ResType.DIMEN_SIZE, resId = R.dimen.default_spacing) static float prop3;
 
 Litho 组件包含两种类型的数据
 props ： 从父组件传递而来，在组件的生命周期中不可以改变
 state ：封装了组件管理的实现细节，对于父组件来说是透明的
 
 声明组件状态
 在组件的生命周期方法中可以通过@State 来声明组件的状态。
 在LayoutSpec 和 MountSpec 的生命周期方法中都可以声明组件状态
 
 初始化State值
 使用@OnCreateInitialState 注解初始化方法，第一个参数必须是ComponentContext,参数的类型必须是StateValue<状态对应的类型>
 状态更新 @OnUpdateState
 
 Litho 使用Yoga在屏幕上测量和布局组件 Yoga是Flexbox 的一种实现
 在Litho中，可以使用Row来实现一个横向的布局，就像横向的LinearLayout
             可以使用Column来实现垂直方向的布局，就像垂直的LinearLayout
 
 为了实现LinearLayout中的权重效果，Flexbox 提供了一个flexGrow(<weight>)概念
 
 如果想实现一个View重叠在另一个View上的效果(overlay)，  Flexbox提供了positionType(ABSOLUTE) 来实现
 
 错误边界 仍然是在测试阶段，默认不可用，当前支持的代理方法：
 onCreateLayout
 onCreateLayoutWithSizeSpec
 onMount计划支持
 
 OnError 代理回调
 
 边框Border 
 Litho支持样式化的组件边框，都通过Border（构建者模式） 对象来配置，
  支持设置每个边的边框宽度
  
  边框效果：
  Dash 虚线模式
  Discrete  离散的
  Path Dash 
  Composition 合成上面的多种效果
  
  RecyclerCollectionComponent
  RecyclerView  是Android 应用用来构建滚动列表的基础构建块，在Litho中推荐使用RecyclerCollectionComponent 和Sections 来构建滚动列表。包括简单列表或者混合列表。
  混合列表比如多个数据源 或增量挂载（incremental mount）
  
  横向列表
  RecyclerCollectionComponent 有一个RecyclerConfiguration属性，用来决定使用哪种布局管理器。 默认使用ListRecyclerConfiguration，它会创建一个垂直方向的 LinearLayoutManager 布局
  对于横向布局，可以传递一个横向的ListRecyclerConfiguration
  
   网格布局 GridRecyclerConfiguration
   
   Snapping 对于横向列表来说，Snapping效果也可以配置 SNAP_NONE, SNAP_TO_END, SNAP_TO_CENTER SNAP_TO_START
   
   设置横向列表高度的三种方式：
   1 固定高度  .height(heightValue) 
   2 当组件创建的时候不知道高度。设置第一项的高度为列表高度，需设置  .canMeasureRecycler(true) 如果既没有设置固定高度也没有设置canMeasureRecycler 为true，列表的高度将为0
   3 让高度随着项中的最高高度而动态改变 代价比较高一般不这样配置
   final RecyclerBinderConfiguration configuration = new RecyclerBinderConfiguration(rangeRatio);
   configuration.setHasDynamicItemHeight((true);
   RecyclerConfiguration recyclerConfiguration =
       new ListRecyclerConfiguration(
           LinearLayoutManager.HORIZONTAL,
           reverseLayout,
           snapMode,
           configuration);
   
   final Component component =
       RecyclerCollectionComponent.create(context)
           .section(FooSection.create(new SectionContext(context)).build())
           .recyclerConfiguration(recyclerConfiguration)
           .canMeasureRecycler(true)
           .build();
  
  下拉刷新（Pull to refresh）
  RecyclerCollectionComponent 默认下拉刷新可用，发送一个事件Handler给底层的Recycler,会触发SectionTree的刷新。禁用该功能，需要设置disablePTR 为true
  
  正在加载(Loading) 空（Empty） 错误（Error）屏
 
  final Component component =
      RecyclerCollectionComponent.create(context)
          .section(FooSection.create(new SectionContext(context)).build())
          .recyclerConfiguration(recyclerConfiguration)
          .loadingComponent(
              Progress.create(c)
                  .build())
          .errorComponent(
              Text.create(c)
                  .text("Data Fetch has failed").build())
          .emptyComponent(
              Text.create(c)
                  .text("No data to show").build())
          .build();

  缓存值
  Cache Value API 提供Spec类内部的缓存，而不是用于耗时计算或惰性状态更新。该API由两个注解组成：
  @CachedValue  @CachedValue和@Prop @State的使用方式一样，用来注解一个参数到Spec方法中 生成的代码会将正确的缓存值传递给Spec方法。
  @OnCalculateCachedValue  @OnCalculateCachedValue 用来计算缓存值，缓存值只能依赖于prop和state不能依赖于其他参数
  
  缓存值是存储在ComponentTree中，因此缓存值的生命周期和ComponentTree一致
  
  变换动画（Transition Animations）
  Litho允许当布局在屏幕上发生变化的时候，声明布局动画。当一个组件出现，消失，属性发生改变的时候，声明动画，当前支持组件的饱和度动画，缩放动画，位置动画
  Litho的动画变换定义在UI的两种状态之间变换
  UI的状态声明和@OnCreateLayout一样，动画需要另一个生命周期方法@OnCreateTransition，其定义了UI应该怎样从一个状态变化到下一个状态。
  
  Interruptible: Animations can be interrupted and driven to a new ending value automatically
  Declarative: The framework handles and drives the animations for you, meaning you get 60fps, interruptible animations without extra work.
  Current Properties: Currently supports animating X, Y, WIDTH, HEIGHT, SCALE ALPHA, and ROTATION.
  Litho 变换支持单个组件 多个组件 或者具有key的多个组件  也可以是针对单个属性 多个属性
  
  Staggers, Sequences, and Parallel Sets
  Staggers 错开 一个完了之后接着一个，指定间隔
  Sequences  一个动画结束之后，另一个才开始
  Parallel 所有的子动画同时开始
  
  反弹（spring ）支持
  
  Tooltips 
  Litho中Tooltips API 提供了在一个锚点组件上显示一个悬浮的View
  
  工具类 LithoTooltipController
  LithoTooltip 是一个接口，要求实现在给定的host view 上显示tooltip ，以及锚点组件相对于host view 的边界。也可以自定义tooltip的实现
  
  Section
