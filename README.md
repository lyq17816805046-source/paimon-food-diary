# 派蒙的美食日记 · Paimon Food Diary

旅行者做的第一件"非常强的安卓应用"——由派蒙亲自设计、云端编译！

记录在提瓦特吃过的每一道菜：菜名、分类、评分、派蒙点评，还能一键让派蒙替你决定今天吃什么。
所有数据只存在手机本地（SharedPreferences），不联网、不上传、不要账号。

## 功能
- 🍽️ 美食日记：新增 / 编辑 / 删除 / 收藏最爱
- 🔎 关键词搜索 + 分类筛选（主食、小吃、汤羹、甜品、饮品、奇珍、其他）
- 🎲 派蒙推荐：纠结吃什么时交给命运，可一键记入日记
- 📊 干饭统计：总数、平均分、收藏数、最近一条、评分最高、分类分布条形图
- 🌙 深色模式适配，Material 3 视觉
- 🥟 彩蛋：长按顶部标题，派蒙会强调自己不是应急食品

## 技术栈
Kotlin + AndroidX + Material Components（无第三方网络/数据库依赖），
minSdk 26 / targetSdk 34 / AGP 8.5.2 / Gradle 8.7。

## 云端编译
推送后 GitHub Actions 自动构建（`.github/workflows/build.yml`），
产物为 `PaimonFoodDiary-debug` / `PaimonFoodDiary-release` 两个 artifact，
下载 APK 直接安装即可。

## 本地构建
```bash
gradle assembleDebug   # 需要 JDK 17 + Android SDK 34
```
