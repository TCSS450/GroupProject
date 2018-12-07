package group3.tcss450.uw.edu.groupappproject.utility;

import android.support.v4.app.Fragment;
import android.widget.EditText;

import group3.tcss450.uw.edu.groupappproject.R;

/**
 * Methods to validate user password selections.
 */
public class PasswordRules {


    public static boolean isValidPasswords(
                Fragment fragment, EditText password1, EditText password2) {

        boolean hasUpperCase, hasLowerCase, hasSpecial, hasNumber, hasError = false;

        if (password1.getText().toString().length() > 0
                && password2.getText().toString().length() > 0) {

            hasUpperCase = password1.getText().toString().chars().filter(Character::isUpperCase)
                                .count() > 0 &&
                            password2.getText().toString().chars().filter(Character::isUpperCase)
                                .count() > 0;

            hasLowerCase = password1.getText().toString().chars().filter(Character::isLowerCase)
                                .count() > 0 &&
                            password2.getText().toString().chars().filter(Character::isLowerCase)
                                .count() > 0;

            hasSpecial = !password1.getText().toString().matches("[A-Za-z0-9]*");

            hasNumber = !password1.getText().toString().matches("[0-9]*");

        } else {
            password1.setError(fragment.getString(R.string.empty));
            password2.setError(fragment.getString(R.string.empty));
            return true;
        }


        if (!hasUpperCase) {
            password1.setError(fragment.getString(R.string.uppercase_error));
            password2.setError(fragment.getString(R.string.uppercase_error));
            hasError = true;
        } else if (!hasLowerCase) {
            password1.setError(fragment.getString(R.string.lowercase_error));
            password2.setError(fragment.getString(R.string.lowercase_error));
            hasError = true;
        } else if (password1.getText().toString().length() < 6) {
            password1.setError(fragment.getString(R.string.passToSmall));
            password2.setError(fragment.getString(R.string.passToSmall));
            hasError = true;
        } else if (!hasSpecial) {
            password1.setError(fragment.getString(R.string.special_error));
            password2.setError(fragment.getString(R.string.special_error));
            hasError = true;
        }  else if (!hasNumber) {
            password1.setError(fragment.getString(R.string.number_error));
            password2.setError(fragment.getString(R.string.number_error));
            hasError = true;
        } else if (!password1.getText().toString().equals(password2.getText().toString())) {
            password2.setError(fragment.getString(R.string.passNotMatch));
            password1.setError(fragment.getString(R.string.uppercase_error));
            hasError = true;
        }

        return hasError;
    }

}
