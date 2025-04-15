dview-skins
![Release](https://jitpack.io/v/dora4/dview-skins.svg)
--------------------------------

#### 卡片

![Dora视图_皮肤自由](https://github.com/user-attachments/assets/6f13ae4f-04ea-4f32-9740-372637261046)
##### 卡名：Dora视图 皮肤自由 
###### 卡片类型：场地魔法
###### 属性：魔法
###### 效果：我方场地上名字带有「Dora视图」的怪兽，在战斗伤害计算阶段攻击力上升1000点。名字带有「Dora视图」的怪兽送去墓地时，可回到卡组最下方。此卡在场上时，我方名字带有「Dora视图」的融合怪兽，不需要「融合」魔法卡。

#### Gradle依赖配置

```groovy
// 添加以下代码到项目根目录下的build.gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
// 添加以下代码到app模块的build.gradle
dependencies {
    implementation 'com.github.dora4:dora:1.3.5'
    implementation 'com.github.dora4:dview-skins:1.9'
}
```

#### 示例代码
https://github.com/dora4/DoraMusic
