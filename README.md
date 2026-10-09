# محاسب سوفت - Muhasib Soft

تطبيق محاسبة متكامل للأندرويد مبني بـ Kotlin و Jetpack Compose

## المميزات

- 📊 إدارة المبيعات والمشتريات
- 📦 تتبع المخزون
- 💰 إدارة الحسابات والقيود اليومية
- 📈 تقارير شاملة
- 🖨️ دعم الطباعة الحرارية
- 🌍 دعم كامل للغة العربية (RTL)

## المتطلبات التقنية

- Android Studio Hedgehog أو أحدث
- Kotlin 1.9.20
- Jetpack Compose
- Material 3
- الحد الأدنى SDK: 24 (Android 7.0)
- الهدف SDK: 34

## المكتبات المستخدمة

- **Room** - قاعدة بيانات محلية
- **Hilt** - حقن التبعيات
- **Navigation Compose** - التنقل بين الشاشات
- **DataStore** - تخزين الإعدادات
- **Coil** - تحميل الصور
- **Coroutines & Flow** - البرمجة غير المتزامنة

## التثبيت

1. استنسخ المستودع:
```bash
git clone https://github.com/YOUR_USERNAME/MuhasibSoft.git
```

2. افتح المشروع في Android Studio

3. انتظر حتى ينتهي Gradle من البناء

4. شغل التطبيق على جهاز حقيقي أو محاكي

## البنية المعمارية

```
app/
├── data/
│   ├── local/          # Room Database
│   ├── model/          # Data Classes
│   └── repository/     # Repository Pattern
├── di/                 # Hilt Modules
└── presentation/
    ├── components/     # Reusable UI Components
    ├── screens/        # App Screens
    ├── theme/          # App Theme
    └── viewmodel/      # ViewModels
```

## الشاشات

- الشاشة الرئيسية
- المبيعات (قائمة + إضافة)
- المشتريات (قائمة + إضافة)
- الحسابات
- العمليات المخزنية
- القيود اليومية
- التقارير
- الإعدادات

## المساهمة

المساهمات مرحب بها! يرجى فتح Issue أولاً لمناقشة التغييرات.

## الترخيص

MIT License

## التواصل

- Email: support@muhasibsoft.com
- Website: https://muhasibsoft.com