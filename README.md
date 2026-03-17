# KD300 - NFC读卡APP

这是一个使用 **Kotlin + Android NFC ReaderMode** 实现的基础 NFC 读卡应用。

## 功能

- 自动检测设备是否支持 NFC
- 提示 NFC 是否开启
- 读取 NFC 卡片 UID
- 显示卡片支持的 Tech 列表（NfcA/NfcB/NfcF/NfcV）
- 尝试解析 NDEF 文本记录（Text Record）

## 项目结构

- `app/src/main/java/com/example/nfcreader/MainActivity.kt`：核心读卡逻辑
- `app/src/main/AndroidManifest.xml`：NFC 权限与能力声明
- `app/src/main/res/layout/activity_main.xml`：界面布局

## 运行方式

1. 使用 Android Studio 打开仓库根目录。
2. 等待 Gradle 同步完成。
3. 使用一台支持 NFC 的 Android 手机连接调试。
4. 安装并打开 APP。
5. 将 NFC 卡贴近手机背面，查看读取结果。

## 注意事项

- 真机测试需要开启系统 NFC。
- 某些门禁卡可能是加密卡，应用只能读取 UID/基础信息，不能直接解密内容。
