package com.example.inovasiyanotebook.views.login;

import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;

public class CustomLoginForm extends LoginForm {
    public CustomLoginForm() {
        // Здесь вы можете получить переводы из вашего I18NProvider и применить их
        setI18n(createLoginI18n());
    }

    private LoginI18n createLoginI18n() {
        LoginI18n i18n = LoginI18n.createDefault();
        i18n.getForm().setTitle("Giriş");
        i18n.getForm().setUsername("İstifadəçi adı");
        i18n.getForm().setPassword("Şifrə");
        i18n.getForm().setSubmit("Daxil ol");
        i18n.getForm().setForgotPassword("");
        // Добавьте другие необходимые переводы
        return i18n;
    }
}
