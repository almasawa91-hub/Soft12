# المساهمة في محاسب سوفت

شكراً لاهتمامك بالمساهمة! 🎉

## كيفية المساهمة

### 1. الإبلاغ عن مشكلة
- افتح Issue جديد مع وصف مفصل للمشكلة
- أرفق خطوات إعادة الإنتاج
- أضف لقطات شاشة إن أمكن
- حدد إصدار التطبيق ونظام Android

### 2. اقتراح ميزة
- افتح Issue بعنوان يبدأ بـ "Feature:"
- اشرح الفائدة من الميزة
- اقترح تصميماً مبدئياً إن أمكن

### 3. إرسال كود (Pull Request)
1. Fork المستودع
2. أنشئ فرعاً جديداً: `git checkout -b feature/my-feature`
3. Commit تغييراتك: `git commit -m 'feat: add my feature'`
4. Push للفرع: `git push origin feature/my-feature`
5. افتح Pull Request

## معايير الكود

- اتبع Kotlin Coding Conventions الرسمية
- استخدم Compose best practices
- اكتب تعليقات بالعربية للأجزاء المعقدة
- أضف Unit Tests للميزات الجديدة
- تأكد من دعم RTL في أي واجهة جديدة

## رسائل Commit

نستخدم Conventional Commits:
- `feat:` ميزة جديدة
- `fix:` إصلاح خطأ
- `docs:` توثيق
- `refactor:` إعادة هيكلة
- `test:` اختبارات
- `chore:` مهام صيانة

## ملاحظات مهمة

- جميع النصوص بالعربية (RTL)
- لا تضف مكتبات ويب أو WebView
- حافظ على الحد الأدنى SDK 24