package com.buzz.user.enums;

public enum Role {
    USER,       // 👤 Normal üye
    PREMIUM,    // 💎 Premium üye (kozmetik avantajlar)
    ADMIN       // 🛡️ Yönetici
}
// 👻 VISITOR = Login olmamış kullanıcı (DB'de tutulmaz, Spring Security anonymous olarak yönetir)