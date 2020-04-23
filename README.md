# FlexibleEditText
这是我上传的第一个Android方向的UI控件，请多指教
## 效果展示
最普通的样式
![](https://github.com/Papa1998/FlexibleEditText/blob/master/image/fet_1.gif)

圆角样式
![](https://github.com/Papa1998/FlexibleEditText/blob/master/image/fet_2.gif)

部分圆角样式
![](https://github.com/Papa1998/FlexibleEditText/blob/master/image/fet_3.gif)

icon支持Tint着色器
![](https://github.com/Papa1998/FlexibleEditText/blob/master/image/fet_4.gif)
 
## 目前支持的特性（v1.0.0）
 - xml中添加FlexibleEditText
 - 自定义icon
 - AS的preview中显示展开后的的样式
##  使用步骤
### 第一步 - 添加依赖
在项目的build.gradle中添加以下依赖，应该都懂吧，这里就不多说了
 - 在根build.gradle下

```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

 1. 在依赖build.gradle中
```
	dependencies {
	        implementation 'com.github.Papa1998:FlexibleEditText:v1.0.0'
	}
```
### 第二步 - 在xml中使用控件
目前只支持在xml布局文件中使用，不过很快会出在java代码中动态添加的方式
```
	<com.papa.library.FlexibleEditText
	        android:layout_width="match_parent"
	        android:layout_height="48dp"
	        android:layout_margin="8dp"
	        app:fet_buttonIcon="你要使用的图标">
	        <EditText
	            android:layout_width="match_parent"
	            android:layout_height="match_parent"/>
	</com.papa.library.FlexibleEditText>
```
`注意事项：添加的子View必须是EditText，并且只能有一个`
### 在Activity中获取Flexible
 1. 声明
```
	private FlexibleEditText fet;
```
2. 获取

```
	fet = findViewById(R.id.fet);
```
然后你就可以随便用了
### xml中的属性详解
| 属性名 | 作用 |
|--|--|
| fet_backgroundColor | 设置背景伸缩条的颜色 |
| fet_corner | 设置背景伸缩条的圆角大小 |
| fet_duration | 设置动画时长 |
| fet_leftTopCornerEnable | 设置左上角是否启用圆角 |
| fet_rightTopCornerEnable | 设置右上角是否启用圆角 |
| fet_leftBottomCornerEnable | 设置左下角是否启用圆角 |
| fet_rightBottomCornerEnable | 设置右下角是否启用圆角 |
| fet_buttonIcon | 设置icon图片 |
| fet_buttonIconTint | 设置icon着色 |
| fet_buttonWidth | 设置icon的宽度 |
| fet_buttonHeight | 设置icon的高度 |
###  更多
 - 联系我
	- 我的邮箱：953005506@qq.com
		我如果有看到邮件会及时回复的，只要不是垃圾邮件哈哈哈哈哈哈
	- 我的[CSDN](https://blog.csdn.net/weixin_42530254)博客
		转载需要署名喔
