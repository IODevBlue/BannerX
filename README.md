BannerX
=======
BannerX is a Kotlin library which provides an intuitive way to display advertisements and slideshows.

[<img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/IODevBlue/BannerX?label=Current Version&color=2CCCE4&style=for-the-badge&labelColor=0109B6">](https://github.com/IODevBlue/BannerX/releases) <img alt="Repository Size" src="https://img.shields.io/github/repo-size/IODevBlue/BannerX?color=2CCCE4&style=for-the-badge&labelColor=0109B6"> [<img alt="License" src="https://img.shields.io/github/license/IODevBlue/BannerX?color=2CCCE4&style=for-the-badge&labelColor=0109B6">](http://www.apache.org/licenses/LICENSE-2.0) [<img alt="GitHub Repository stars" src="https://img.shields.io/github/stars/IODevBlue/BannerX?color=2CCCE4&style=for-the-badge&labelColor=0109B6">](https://github.com/IODevBlue/BannerX/stargazers)
<img alt="GitHub watchers" src="https://img.shields.io/github/watchers/IODevBlue/BannerX?label=Repository Watchers&color=2CCCE4&style=for-the-badge&labelColor=0109B6"> [<img alt="Gradle version" src="https://img.shields.io/static/v1?label=Gradle version&message=7.5.1&color=2CCCE4&style=for-the-badge&labelColor=0109B6">](https://docs.gradle.org/7.5.1/release-notes) [<img alt="Kotlin version" src="https://img.shields.io/static/v1?label=Kotlin version&message=1.7.10&color=2CCCE4&style=for-the-badge&labelColor=0109B6">](https://KOTLINlang.org/docs/whatsnew1720)

Table of content
----------------
- [Documentations](https://github.com/IODevBlue/BannerX/tree/main#documentations)
- [Uses](https://github.com/IODevBlue/BannerX#uses)
- [Features](https://github.com/IODevBlue/BannerX#features)
- [Installation](https://github.com/IODevBlue/BannerX#installation)
- [Usage](https://github.com/IODevBlue/BannerX#usage)
- [Java interoperability](https://github.com/IODevBlue/BannerX#java-interoperability)
- [XML attributes](https://github.com/IODevBlue/BannerX#xml-attributes)
- [Applications using BannerX](https://github.com/IODevBlue/BannerX#applications-using-bannerx)
- [Contributions](https://github.com/IODevBlue/BannerX#contributions)
- [Changelog](https://github.com/IODevBlue/BannerX#changelog)
- [License](https://github.com/IODevBlue/BannerX#license)

Dependencies
------------
The sample implementation of `BannerX` uses an unofficial [Pexel SDK](https://github.com/SanjayDevTech/pexels-android) written in Kotlin for native android by [SanjayDevTech](https://github.com/SanjayDevTech).
Also, the sample implementation contains a Playground to test common features in `BannerX`.

Documentations
--------------
Documentations are available online in [Javadoc](https://raw.githack.com/IODevBlue/project-docs/main/api/android/bannerx/javadoc/index.html) and [KDoc](https://raw.githack.com/IODevBlue/project-docs/main/api/android/bannerx/html/index.html).

Uses
----
BannerX has a TON of usages:
- OnBoarding and Introductory slides.
- Advertisements and slideshows. 
- Video previews.
- GIFs slides.

...and so much more.

<p align="center"><img src="/art/main.gif" alt="main"></p>

Features
--------
- Highly customizable.
- No hidden reflection usage.
- Uses an internal [ViewPager2](https://developer.android.com/reference/kotlin/androidx/viewpager2/widget/ViewPager2) to handle scrolling and swiping of banners.
- Handles state management.
- Supports custom views.
- Infinite swiping and looping.
- Custom [transformers](https://github.com/IODevBlue/BannerX-Transformers) and [indicators](https://github.com/IODevBlue/BannerX-Indicators).
- Double and long click support.
- More control over auto-swiping speed and auto-looping speed.
- Internal compression algorithm for images with large dimensions.

Installation
------------
1. Grab an artifact from the Maven Central Repository:
```GROOVY
implementation 'io.github.iodevblue:bannerx:1.0.0'
```
- On Apache Maven
```XML
<dependency>
  <groudId> io.github.iodevblue </groudId>
  <artifactId> bannerx </artifactId>
  <version> 1.0.0 </version>
</dependency>
```
If it is a snapshot version, add the snapshot Maven Nexus OSS repository:
```GROOVY
maven {   
  url 'https://s01.oss.sonatype.org/content/repositories/snapshots/'
}
```
Then retrieve a copy:
```GROOVY
implementation 'io.github.iodevblue:bannerx:1.0.0-SNAPSHOT'
```

Usage
-----
Check the [XML attributes](https://github.com/IODevBlue/BannerX#xml-attributes) section for all available `BannerX` xml attributes.

Add `Bannerx` to an XML layout.
```xml
    <com.blueiobase.api.android.bannerx.BannerX
        android:id="@+id/bannerx"
        android:layout_width="match_parent"
        android:layout_height="250dp"
        app:autoLoopDelay="3000"
        app:autoLoopSpeed="800"
        app:bannerBottomMargin="30dp"
        app:indicatorBackground="@android:color/transparent"
        app:indicatorFadeOnIdle="true"
        app:indicatorHorizontalArrangement="CENTER"
        app:indicatorTextColor="@color/black"
        app:isManualLoopable="true"
        app:numberOfStubs="3"
        app:showIndicatorText="true" />

```
Retrieve `BannerX` in code.
```kotlin
 val bannerx by lazy { findViewById<BannerX>(R.id.bannerx) }
```

`BannerX` requires a list of `Banner` objects to display. Each `Banner` can contain a title, image, a specific background and an optional [ShapeAppearanceModel](https://developer.android.com/reference/com/google/android/material/shape/ShapeAppearanceModel) to define the corners of the image.
```kotlin
val list = listOf(
  Banner("Banner1").let {
    it.image = Bitmap.createBitmap()
    it.background = ColorDrawable(Color.parseColor("#000000")) 
  }, 
  Banner("Banner2").let {
    it.image =  ContextCompat.getDrawable(context, R.drawable.ic_baseline_play_arrow_24)
    it.background = ColorDrawable(Color.parseColor("#FF0000"))
    it.shapeAppearanceModel = ShapeAppearanceModel.Builder().setAllCornerSizes(200F).build()
  },
  Banner(),
  Banner("Banner4").let {
    it.background = ColorDrawable(Color.parseColor("#0109B6"))
  })

bannerx.processList(list) //There are overloaded variants of the processList() function.
```
That's all there is to `BannerX`.

**NOTE:** Each `Banner` image is implementation specific and it can be a `Drawable`, a `Drawable` resource, a local file, a url `String`, `Uri` etc.
it is highly recommended to make the `Banner`'s image a `Drawable`, a local `Uri` or a `Bitmap` which is internally detected in `BannerX`.

By default, `BannerX` provides a generic layout for each `Banner` object. However, if you require more detailed control over what appearance/layout `Banner` objects 
should have, you can provide a `CustomAdapter` implementation to handle your requirements. 
This is more preferable if you delegate image displays to 3rd party libraries such as Glide, Fresco, Picasso etc. 

`CustomAdapter` implementations follow the `RecyclerView.Adapter` pattern.

Create a custom layout for `Banner` objects: `custom_banner_layout.xml`
```xml
<RelativeLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    >
    <ImageView
        android:id="@+id/banner_image_full"
        android:contentDescription="@string/image_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        />
    <ImageView
        android:id="@+id/like"
        android:layout_width="150dp"
        android:layout_height="150dp"
        android:layout_centerInParent="true"
        android:src="@drawable/ic_baseline_favorite_24"
        android:alpha="0"
        android:scaleX="1"
        android:scaleY="1"
        />

</RelativeLayout>
```
Extend the `BannerViewHolder` class providing a `View` constructor parameter:
```kotlin
private class CustomBannerViewHolder(view: View): BannerX.BannerViewHolder(view) { 
  val image: ImageView by lazy { view.findViewById<ImageView>(R.id.banner_image_full) }
  val like: ImageView by lazy { findViewById<ImageView>(R.id.like)  }
}
```
Instruct `BannerX` to use the `CustomAdapter` implementation by invoking the `useCustomAdapter()`.

**NOTE:** The `useCustomAdapter()` must be called before the providing a list to `BannerX` through `processList()`. For easy setup, the `processList()` can be chained to the `useCustomAdapter()` function:
```kotlin
val list = listOf(
  Banner("Banner1").let {
    it.image = Bitmap.createBitmap()
    it.background = ColorDrawable(Color.parseColor("#000000"))
  },
  Banner("Banner2").let {
    it.image =  ContextCompat.getDrawable(context, R.drawable.ic_baseline_play_arrow_24)
    it.background = ColorDrawable(Color.parseColor("#FF0000"))
    it.shapeAppearanceModel = ShapeAppearanceModel.Builder().setAllCornerSizes(200F).build()
  },
  Banner(),
  Banner("Banner4").let {
    it.background = ColorDrawable(Color.parseColor("#0109B6"))
  })

bannerx.useCustomAdapter(
    object: BannerX.CustomAdapter { 
        override fun getBannerType(position: Int) = 0 //Return zero if there would be no type differentiation
      
        override fun onCreateBannerViewHolder(parent: ViewGroup, viewType: Int): BannerX.BannerViewHolder { //Create an instance of your BannerViewHolder implementation.
          return CustomBannerViewHolder(layoutInflater.inflate(R.layout.custom_banner_layout, parent, false)) 
        }
      
        override fun onBindBannerViewHolder(bannerViewHolder: BannerX.BannerViewHolder, position: Int) {  //Bind Banner data to your BannerViewHolder implementation. 
          (bannerViewHolder as CustomBannerViewHolder).apply { 
            image.setImageBitmap(list[position].image as Bitmap) 
          } 
        }
    }
).processList(list, forceUpdateIfEmpty = false)
```
For more details, check the [FullscreenActivity](https://github.com/IODevBlue/BannerX/tree/main/sample/src/main/kotlin/com/blueiobase/api/android/bannerx/sample/ui/activity/FullscreenActivity.kt) class from the [sample](https://github.com/IODevBlue/BannerX/tree/main/sample) module which uses a `CustomAdapter` to display `Banner` objects.

**NOTE:** Using a `CustomAdapter` to manually handle `Banner` displays disables scaling of `Banner` objects when clicked or touched. However, the scale-on-touch facility can still be applied if there's need for it using the static function `BannerX.doScaleAnimateOn()`.
```kotlin
private val nextButton: ImageButton by lazy { findViewById(R.id.next) }

BannerX.doScaleAnimateOn(nextButton, BannerScaleAnimateParams(), onClickListener = {
  Toast.makeText(context, "Next button clicked", Toast.LENGTH_SHORT).show()
})
```
`BannerX.doScaleAnimateOn()` function can also be used outside `BannerX`.

Apply a custom `BannerXTransformer`:
```kotlin
bannerx.bannerXTransformer = DefaultBannerTransformer()
```
For more `BannerXTransformers`, check the [BannerX-Transformers](https://github.com/IODevBlue/BannerX-Transformers) repository.

A `BannerXTransformer` is simply a [ViewPager2.PageTransformer](https://developer.android.com/reference/kotlin/androidx/viewpager2/widget/ViewPager2.PageTransformer) specifically designed for `BannerX`. However, if the transformers
currently available for `BannerX` do not suit your needs, you can provide your own `ViewPager2.PageTransformer` using the `setCustomPageTransformer()` function. 
```kotlin
bannerx.setCustomPageTransformer(
  object: ViewPager2.PageTransformer { //Apply your page transformations in the `transformPage()` function.
    override fun transformPage(banner: View, position: Float) { }
  }, true)
```
The boolean in the `setCustomPageTransformer()` function informs `BannerX` to continue on the current `Banner`, `false` to start from beginning.

**NOTE:** Unlike a `BannerXTransformer`, custom `ViewPager2.PageTransformers` do not survive state and configuration changes.

If you do make interesting page transformers, do consider contributing to [BannerX-Transformers](https://github.com/IODevBlue/BannerX-Transformers).


Apply a custom `BannerXIndicator`:
```kotlin
bannerx.bannerXIndicator = DefaultBannerIndicator()
```
For more `BannerXIndicators`, check the [BannerX-Indicators](https://github.com/IODevBlue/BannerX-Indicators) repository.

Listen for changes in scrolling/swiping on `BannerX` using an `OnBannerChangeListener` implementation. It follows the `ViewPager2.OnPageChangeCallback` pattern:
```kotlin
bannerx.onBannerChangeListener = object: BannerX.OnBannerChangeListener {
  override fun onBannerScrollStateChanged(state: ScrollState) {} //Callback invoked when a drag operation is happening on BannerX. The ScrollState is the current scrolling state of BannerX. 
  override fun onBannerScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {} //Callback invoked when the current Banner is being scrolled
  override fun onBannerSelected(position: Int) {} //Callback invoked when a Banner is selected.
}
```

Listen for clicks on `Banner` objects using an `OnBannerClickListener` implementation:
```kotlin
bannerx.onBannerClickListener = object: BannerX.OnBannerClickListener {
  override fun onBannerClick(banner: Banner, view: View, position: Int) {
      //React to clicks on Banner objects here.
  }
}
```
or apply a click listener easily using Kotlin's receiver function syntax:
```kotlin
bannerx.setOnBannerClickListener { banner, view, position ->
  //React to clicks on Banner objects here.
}
```

Listen for double clicks on `Banner` objects using an `OnBannerDoubleClickListener` implementation:
```kotlin
bannerx.onBannerDoubleClickListener = object: BannerX.OnBannerDoubleClickListener {
  override fun onBannerDoubleClick(banner: Banner, view: View, position: Int) {
      //React to double clicks on Banner objects here.
  }
}
```
or apply a double click listener easily using Kotlin's receiver function syntax:
```kotlin
bannerx.setOnBannerDoubleClickListener { banner, view, position ->
  //React to double clicks on Banner objects here.
}
```

Listen for long clicks on `Banner` objects using an `OnBannerLongClickListener` implementation:
```kotlin
bannerx.onBannerLongClickListener = object: BannerX.OnBannerLongClickListener {
  override fun onBannerLongClickListener(banner: Banner, view: View, position: Int) {
      //React to long clicks on Banner objects here.
  }
}
```
or apply a long click listener easily using Kotlin's receiver function syntax:
```kotlin
bannerx.setOnBannerLongClickListener { banner, view, position ->
  //React to long clicks on Banner objects here.
}
```
Check the sample implementation for more details.


Java Interoperability
---------------------
BannerX is completely interoperable with Java and designed to support JVM overloads.

Add `Bannerx` to an XML layout.
```xml
    <com.blueiobase.api.android.bannerx.BannerX 
        android:id="@+id/bannerx"
        android:layout_width="match_parent"
        android:layout_height="250dp"
        app:autoLoopDelay="3000"
        app:autoLoopSpeed="800"
        app:bannerBottomMargin="30dp"
        app:indicatorBackground="@android:color/transparent"
        app:indicatorFadeOnIdle="true"
        app:indicatorHorizontalArrangement="CENTER"
        app:indicatorTextColor="@color/black"
        app:isManualLoopable="true"
        app:numberOfStubs="3"
        app:showIndicatorText="true" />

```
Retrieve `BannerX` in code.
```java
private BannerX bannerx;

@Override
protected void onCreate (@Nullable Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  bannerx = findViewById(R.id.bannerx);
}
```

`BannerX` requires a list of `Banner` objects to display. Each `Banner` can contain a title, image, a specific background and an optional [ShapeAppearanceModel](https://developer.android.com/reference/com/google/android/material/shape/ShapeAppearanceModel) to define the corners of the image.
```java
List<Banner> list = new ArrayList<>();

Banner b1 = new Banner("Banner1");
b1.setImage(Bitmap.createBitmap((Bitmap) null));
b1.setBackground(new ColorDrawable(Color.parseColor("#000000")));
		
Banner b2 = new Banner("Banner2");
b2.setImage(ContextCompat.getDrawable(context, R.drawable.ic_baseline_play_arrow_24));
b2.setBackground(new ColorDrawable(Color.parseColor("#FF0000")));
b2.setShapeAppearanceModel(new ShapeAppearanceModel.Builder().setAllCornerSizes(200F).build());

Banner b3 = new Banner();

Banner b4 = new Banner("Banner1");
b4.setBackground(new ColorDrawable(Color.parseColor("#0109B6")));
	
list.add(b1);
list.add(b2);
list.add(b3);
list.add(b4);

bannerx.processList(list); //There are overloaded variants of the processList() function.
 ```
That's all there is to `BannerX`.

**NOTE:** Each `Banner` image is implementation specific and it can be a `Drawable`, a `Drawable` resource, a local file, a url `String`, `Uri` etc.
it is highly recommended to make the `Banner`'s image a `Drawable`, a local `Uri` or a `Bitmap` which is internally detected in `BannerX`.

By default, `BannerX` provides a generic layout for each `Banner` object. However, if you require more detailed control over what appearance/layout `Banner` objects
should have, you can provide a `CustomAdapter` implementation to handle your requirements.
This is more preferable if you delegate image displays to 3rd party libraries such as Glide, Fresco, Picasso etc.

`CustomAdapter` implementations follow the `RecyclerView.Adapter` pattern.

Create a custom layout for `Banner` objects: `custom_banner_layout.xml`
```xml
<RelativeLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    >
    <ImageView
        android:id="@+id/banner_image_full"
        android:contentDescription="@string/image_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        />
    <ImageView
        android:id="@+id/like"
        android:layout_width="150dp"
        android:layout_height="150dp"
        android:layout_centerInParent="true"
        android:src="@drawable/ic_baseline_favorite_24"
        android:alpha="0"
        android:scaleX="1"
        android:scaleY="1"
        />

</RelativeLayout>
```
Extend the `BannerViewHolder` class providing a `View` constructor parameter:
```java
private class CustomBannerViewHolder extends BannerX.BannerViewHolder {

  private ImageView image;
  private ImageView like;

  public CustomBannerViewHolder (@NonNull View itemView) {
    super(itemView);
    image = itemView.findViewById(R.id.banner_image_full);
    like = itemView.findViewById(R.id.like);
  }
} 
```
Instruct `BannerX` to use the `CustomAdapter` implementation by invoking the `useCustomAdapter()`.

**NOTE:** The `useCustomAdapter()` must be called before the providing a list to `BannerX` through `processList()`. For easy setup, the `processList()` can be chained to the `useCustomAdapter()` function:
```java
List<Banner> list = new ArrayList<>();

Banner b1 = new Banner("Banner1"); 
b1.setImage(Bitmap.createBitmap((Bitmap) null));
b1.setBackground(new ColorDrawable(Color.parseColor("#000000")));

Banner b2 = new Banner("Banner2");
b2.setImage(ContextCompat.getDrawable(context, R.drawable.ic_baseline_play_arrow_24));
b2.setBackground(new ColorDrawable(Color.parseColor("#FF0000")));
b2.setShapeAppearanceModel(new ShapeAppearanceModel.Builder().setAllCornerSizes(200F).build());

Banner b3 = new Banner();

Banner b4 = new Banner("Banner1");
b4.setBackground(new ColorDrawable(Color.parseColor("#0109B6")));

list.add(b1);
list.add(b2);
list.add(b3);
list.add(b4);

bannerx.useCustomAdapter(new BannerX.CustomAdapter() {
  @Override 
  public int getBannerType (int i) {
	return 0; //Return zero if there would be no type differentiation
  }

  @NonNull
  @Override
  public BannerX.BannerViewHolder onCreateBannerViewHolder (@NonNull ViewGroup viewGroup, int i) { //Create an instance of your BannerViewHolder implementation.
	return new CustomBannerViewHolder(getLayoutInflater().inflate(R.layout.custom_banner_layout, viewGroup, false));
  }

  @Override
  public void onBindBannerViewHolder (@NonNull BannerX.BannerViewHolder bannerViewHolder, int position) { //Bind Banner data to your BannerViewHolder implementation. 
	CustomBannerViewHolder customBannerViewHolder = (CustomBannerViewHolder) bannerViewHolder;
	customBannerViewHolder.image.setImageBitmap((Bitmap) list.get(position).getImage());
  }
}).processList(list, false);
```
**NOTE:** Using a `CustomAdapter` to manually handle `Banner` displays disables scaling of `Banner` objects when clicked or touched. However, the scale-on-touch facility can still be applied if there's need for it using the static function `BannerX.doScaleAnimateOn()`.
```java
private ImageButton nextButton;

@Override
protected void onCreate (@Nullable Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  nextButton = findViewById(R.id.next);
  BannerX.doScaleAnimateOn(nextButton, new BannerScaleAnimateParams(), view -> {
	Toast.makeText(context, "Next button clicked", Toast.LENGTH_SHORT).show() }, null, null );
}
```
`BannerX.doScaleAnimateOn()` function can also be used outside `BannerX`.

Apply a custom `BannerXTransformer`:
```java
	bannerx.setBannerXTransformer(new DefaultBannerTransformer());
```
For more `BannerXTransformers`, check the [BannerX-Transformers](https://github.com/IODevBlue/BannerX-Transformers) repository.

A `BannerXTransformer` is simply a [ViewPager2.PageTransformer](https://developer.android.com/reference/kotlin/androidx/viewpager2/widget/ViewPager2.PageTransformer) specifically designed for `BannerX`. However, if the transformers
currently available for `BannerX` do not suit your needs, you can provide your own `ViewPager2.PageTransformer` using the `setCustomPageTransformer()` function.
```java
bannerx.setCustomPageTransformer(new ViewPager2.PageTransformer() {
  @Override
  public void transformPage (@NonNull View page, float position) { //Apply your page transformations in the transformPage() function.

  }, false);
```
or concisely using lambda:
```java
bannerx.setCustomPageTransformer((page, position) -> {
	  //Apply your page transformations here.
  }, false);
```
The boolean in the `setCustomPageTransformer()` function informs `BannerX` to continue on the current `Banner`. `false` to start from beginning.

**NOTE:** Unlike a `BannerXTransformer`, custom `ViewPager2.PageTransformers` do not survive state and configuration changes.

If you do make interesting page transformers, do consider contributing to [BannerX-Transformers](https://github.com/IODevBlue/BannerX-Transformers).

Apply a custom `BannerXIndicator`:
```java
	bannerx.setBannerXIndicator(new DefaultBannerIndicator());
```
For more `BannerXIndicators`, check the [BannerX-Indicators](https://github.com/IODevBlue/BannerX-Indicators) repository.

Listen for changes in scrolling/swiping on `BannerX` using an `OnBannerChangeListener` implementation. It follows the `ViewPager2.OnPageChangeCallback` pattern:
```java
bannerx.setOnBannerChangeListener(new BannerX.OnBannerChangeListener() {
  @Override
  public void onBannerScrolled (int i, float v, int i1) { } //Callback invoked when the current Banner is being scrolled

  @Override
  public void onBannerScrollStateChanged (@NonNull ScrollState scrollState) { } //Callback invoked when a drag operation is happening on BannerX. The ScrollState is the current scrolling state of BannerX.

  @Override
  public void onBannerSelected (int i) { } //Callback invoked when a Banner is selected.
 
});
```

Listen for clicks on `Banner` objects using an `OnBannerClickListener` implementation:
```java
bannerx.setOnBannerClickListener(new BannerX.OnBannerClickListener() {
  @Override
  public void onBannerClick (@NonNull Banner banner, @NonNull View view, int i) {
	//React to clicks on Banner objects here.
  }
});
```
or apply a click listener easily with a lambda:
```java
bannerx.setOnBannerClickListener((banner, view, position) -> {
  //React to clicks on Banner objects here.
});
```

Listen for double clicks on `Banner` objects using an `OnBannerDoubleClickListener` implementation:
```java
bannerx.setOnBannerDoubleClickListener(new BannerX.OnBannerDoubleClickListener() {
  @Override
  public void onBannerDoubleClick (@NonNull Banner banner, @NonNull View view, int i) {
    //React to double clicks on Banner objects here.
  }
});
```
or apply a double click listener easily with a lambda:
```java
bannerx.setOnBannerDoubleClickListener((banner, view, position) -> {
  //React to double clicks on Banner objects here.
});
```

Listen for long clicks on `Banner` objects using an `OnBannerLongClickListener` implementation:
```java
bannerx.setOnBannerLongClickListener(new BannerX.OnBannerLongClickListener() {
  @Override
  public void onBannerLongClickListener (@NonNull Banner banner, @NonNull View view, int i) {
    //React to long clicks on Banner objects here.
  }
});
```
or apply a long click listener easily with a lambda:
```kotlin
bannerx.setOnBannerLongClickListener((banner, view, i) -> {
  //React to long clicks on Banner objects here.
});
```


XML attributes:
--------------
Related attributes have common names associated with them for easier identification:
- loop: Concerning auto-looping.
- stub: Concerning stub `Banner` objects.
- bannerX: Concerning attributes that affect `BannerX` as a whole.
- indicator: Concerning indicator features.
- banner: Concerning `Banner` objects only.
- clipMode: Concerning a feature where `Banner` objects are clipped at both ends to reveal the previous and next `Banner`.
- onClickScale: Concerning scale animations that happen when a `Banner` is touched or clicked.

|Attribute |Default |Use |
|:---|:---:|:---:|
|`isAutoLoopable` |true |Enable or disable auto-looping/slideshows. |
|`isManualLoopable`|false|Enable or disable user initiated infinite scrolling. |
|`autoLoopDelay`|5000 |The time it takes for `BannerX` to swap `Banner` objects. |
|`autoLoopSpeed`|1000 |The swiping speed applied during auto-looping. |
|`isSwipeable`|true |Make BannerX swipeable and interactive. |
|`displayStubsOnStart`|true |Display stub `Banner` objects when no data is available immediately `BannerX` starts for the first time. The `bannerPlaceholderDrawable` attribute should be overridden to display custom images for these stub banners else one would be provided by `BannerX`. |
|`numberOfStubs`|5 |The number of stub `Banner` objects to display when `BannerX` is initialized for the first time. This is only applied if `displayStubsOnStart` is true. **NOTE:** Maximum of 5 stubs. |
|`bannerXOrientation`|HORIZONTAL |The orientation and arrangement of banners in `BannerX`. |
|`bannerXDirection`|LTR |The direction of movement when auto-looping is enabled. |
|`allowIndicator`|true |Enable or disable the indicator widget. |
|`applyMarqueeOnIndicatorText`|false|Truncate any long text by appending a '...' at the end it.|
|`indicatorUnselectedDrawable`|Default light grey circle (#40E4E4E4) |The drawable used by the indicator widget to indicate an unselected banner. |
|`indicatorSelectedDrawable`|Default white circle |The drawable used by the indicator widget to indicate a selected banner.|
|`indicatorHorizontalArrangement`|CENTER |The horizontal arrangement of the indicator panels. |
|`indicatorVerticalAlignment`|BOTTOM |The vertical alignment of the indicator. |
|`indicatorBackground`|Grey/Ash (#40939393) |The background of the indicator. |
|`indicatorStartEndPadding`|3dp |The uniform padding value applied to the inner horizontal bounds at the Start and End positions of the indicator. |
|`indicatorTopBottomPadding`|6dp |The uniform padding value applied to the inner vertical bounds at the Top and Bottom positions of the indicator. |
|`indicatorStartEndMargin`|10dp |The uniform margin value applied to the outer horizontal bounds at the Start and End positions of the indicator. |
|`indicatorTextColor`|White (#FFFFFFFF)|The color of texts used in the indicator. |
|`indicatorTextSize`|15sp |The size of texts used in the indicator. |
|`indicatorTextFont`|Default font |The font applied to texts used in the indicator. |
|`indicatorFadeOnIdle`|false |Apply a fade animation when the indicator is idle (when `BannerX` is not swiping). |
|`indicatorFadeOnIdleDuration`|3000 |The time it takes for the indicator to be visible when idle before it fades away. |
|`allowNumberIndicator`|false |Enable or disable displaying number of banners on the indicator widget. |
|`numberIndicatorBackground`|Grey/Ash (#40939393) |The background of the number indicator. |
|`showIndicatorText`|false |Enable or disable the indicator widget's TextView. |
|`isBannerClickable`|true |Enable or disable clicks on each banner. |
|`bannerPlaceholderDrawable`|<p align="center"><img src="/art/ph.png" alt="placeholder"></p>|The resource ID for the drawable acting as a placeholder for banners with no available display image. |
|`bannerPlaceholderDrawableTint`|Blue (#F20109B6)|The tint applied to the banner placeholder drawable. |
|`bannerDefaultBackground`|Grey/Ash (#40E4E4E4)|The drawable used as the default background for banner images. |
|`bannerImageCompress`|false |Apply compression to banner images if the dimensions are too large >(1012 x 1216). |
|`bannerImageCompressMaxWidth`|1012 |The maximum width of banner images when compression is enabled. |
|`bannerImageCompressMaxHeight`|1216 |The maximum height of banner images when compression is enabled. |
|`applyBannerImageCornerRadius`|true | Enable or disable applying corner radius to each banner image. |
|`bannerImageCornerRadius`|12dp |The radius applied to all corners of each banner image. For more flexible customization options, consider using `setShapeAppearanceModel()` in code. |
|`bannerBottomMargin`|0dp |The margin between the bottom of the displayed banners and the top of the indicator widget. |
|`bannerImageScaleType`|FIT_XY |The image scale type to apply to a Banner images. |
|`applyBannerOnClickScale`|true |Apply a scale animation (punch or expand) to the banner when it is touched or clicked. For more flexibility, provide a `BannerScaleAnimateParams` using `setBannerScaleAnimateParams()` in code. |
|`bannerOnClickScale`|0.955 |The factor applied to the scale animation on the banner. A value greater than 1.0 applies an expand animation, a value less than 1.0 applies a punch animation and exactly 1.0 disables scale animations. The `applyBannerOnClickScale` attribute has to be set to true before this value would have any effect. |
|`bannerOnClickScaleDuration`|50 |The duration of the scale animation. The `applyBannerOnClickScale` attribute has to be set to true before this value would have any effect. |
|`bannerOnClickScaleReleaseDuration`|125 |The duration of the release animation. The `applyBannerOnClickScale` attribute has to be set to true before this value would have any effect. |
|`isClipMode`|false |Enables and disables clip mode. This is when `BannerX` removes padding values when scrolling and applies it when scrolling stops. |
|`clipModeLeftBannerMargin`|30dp |The amount of onscreen visibility the left banner would get when it is not centered during clip mode. |
|`clipModeRightBannerMargin`|30dp |The amount of onscreen visibility the right banner would get when it is not centered during clip mode. |
|`clipModeTopBannerXMargin`|10dp |The padding value applied to the Top position of `BannerX` when clip mode is enabled. |
|`clipModeOnIndicator`|false |Apply the left and right banner clip margin values on the indicator widget when clip mode is enabled. |

Applications using BannerX
--------------------------
If your application uses `BannerX` and you'd love to showcase, send an <a href="mailto:iodevblue@gmail.com">email</a> containing:
- An app icon
- A link to download your application.
- A related Github repository.

Contributions
-------------
Contributors are welcome!

**NOTE:** This repository is split into two branches:
- [main](https://github.com/IODevBlue/BannerX/tree/main) branch
- [development](https://github.com/IODevBlue/BannerX/tree/development) branch
All developing implementations and proposed changes are pushed to the [development](https://github.com/IODevBlue/BannerX/tree/development) branch and finalized updates are pushed to the [main](https://github.com/IODevBlue/BannerX/tree/main) branch.

To note if current developments are being made, there would be more commits in the [development](https://github.com/IODevBlue/BannerX/tree/development) branch than in the [main](https://github.com/IODevBlue/BannerX/tree/main) branch.

Check the [Contributing](https://github.com/IODevBlue/BannerX/blob/development/CONTRIBUTING.md) for more information.


Changelog
---------
* **1.0.0**
    * Initial release

More version history can be gotten from the [Change log](https://github.com/IODevBlue/BannerX/blob/main/CHANGELOG.md) file.


License
=======
```
    Copyright 2023 IO DevBlue

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
```