# محاسب سوفت — Muhasib Soft

تطبيق محاسبة للأندرويد مبني باستخدام Kotlin وJetpack Compose.

## الميزات الحالية في المستودع
- إدارة المبيعات والمشتريات.
- متابعة المخزون والعمليات المخزنية.
- الحسابات والقيود اليومية والسندات.
- التقارير والتصدير والطباعة الحرارية.
- دعم الواجهة العربية واتجاه RTL.
- قاعدة بيانات محلية باستخدام Room.

> ملاحظة: وجود ملفات الشاشات لا يعني وحده أن كل المسارات والميزات مكتملة أو مختبرة. راجع نتائج GitHub Actions قبل اعتماد أي إصدار.

## المتطلبات
- JDK 17.
- Android SDK مع منصة Android 34 وأدوات البناء.
- Gradle 8.4 (يستخدم مشغّل المشروع `gradlew`).
- الحد الأدنى Android SDK 24.

## البناء والاختبار

يتم التحقق من البناء والاختبارات تلقائياً عبر GitHub Actions عند كل تحديث.
على Linux أو macOS:
```bash
git clone https://github.com/almasawa91-hub/Soft12.git
cd Soft12
chmod +x ./gradlew
./gradlew build
./gradlew test
./gradlew assembleDebug
```

على Windows استخدم `gradlew.bat` من مجلد المشروع.

ينشئ البناء التجريبي APK في:
`app/build/outputs/apk/debug/app-debug.apk`

## بنية المشروع
```
app/src/main/java/com/aalmoghalis/muhasibsoft/
├── data/          # نماذج البيانات، Room، المستودعات
├── di/            # حقن التبعيات
├── presentation/  # الشاشات وViewModels والتنقل
├── utils/         # أدوات مساعدة وتصدير
└── workers/       # المهام الخلفية
```

## الأمان
- كلمات مرور المستخدمين الجديدة تُخزّن على شكل تجزئة مملحة PBKDF2-HMAC-SHA256، وليس كنص صريح.
- يجب حماية النسخ الاحتياطية وملفات APK وبيانات قاعدة البيانات عند مشاركتها.
- لا تُدرج مفاتيح توقيع أو أسرار أو بيانات مستخدمين حقيقية في المستودع.

## المساهمة والترخيص
راجع [CONTRIBUTING.md](CONTRIBUTING.md). الترخيص: MIT.
