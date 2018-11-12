package group3.tcss450.uw.edu.groupappproject.utility;

import android.support.v4.app.Fragment;
import android.widget.EditText;

import group3.tcss450.uw.edu.groupappproject.R;

public class PasswordRules {


    public static boolean attemptRegister(
                Fragment fragment, EditText password1, EditText password2) {

        boolean hasUpperCase, hasLowerCase, hasSpecial, hasNumber, hasError = true;

        if (password1.getText().toString().length() > 0
                && password2.getText().toString().length() > 0) {

            hasUpperCase = !password1.getText().toString()
                            .equals(password2.getText().toString().toLowerCase());

            hasLowerCase = !password1.getText().toString()
                     .equals(password2.getText().toString().toUpperCase());

            hasSpecial = !password1.getText().toString().matches("[A-Za-z0-9]*");

            hasNumber = !password1.getText().toString().matches("[0-9]*");

        } else {
            password1.setError(fragment.getString(R.string.empty));
            password2.setError(fragment.getString(R.string.empty));
            return false;
        }


        if (!hasUpperCase) {
            password1.setError(fragment.getString(R.string.uppercase_error));
            password2.setError(fragment.getString(R.string.uppercase_error));

        } else if (!hasLowerCase) {
            password1.setError(fragment.getString(R.string.lowercase_error));
            password2.setError(fragment.getString(R.string.lowercase_error));

        } else if (password1.getText().toString().length() < 6) {
            password1.setError(fragment.getString(R.string.passToSmall));
            password2.setError(fragment.getString(R.string.passToSmall));

        } else if (!hasSpecial) {
            password1.setError(fragment.getString(R.string.special_error));
            password2.setError(fragment.getString(R.string.special_error));

        }  else if (!hasNumber) {
            password1.setError(fragment.getString(R.string.number_error));
            password2.setError(fragment.getString(R.string.number_error));

        } else if (!password1.getText().toString().equals(password2.getText().toString())) {
            password2.setError(fragment.getString(R.string.passNotMatch));
            password1.setError(fragment.getString(R.string.uppercase_error));

        } else {
            hasError = true;
        }
        return hasError;
    }

}
