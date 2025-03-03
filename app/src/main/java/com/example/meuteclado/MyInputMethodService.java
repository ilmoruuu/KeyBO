package com.example.meuteclado;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.inputmethodservice.InputMethodService;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.view.textservice.SentenceSuggestionsInfo;
import android.view.textservice.SuggestionsInfo;
import android.view.textservice.TextServicesManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.view.textservice.SpellCheckerSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyInputMethodService extends InputMethodService {

    private boolean isShifted = false;
    private boolean isNumericMode = false;
    private View alphabeticKeyboardView;
    private View numericKeyboardView;
    private Handler deleteHandler;
    private Runnable deleteRunnable;
    private boolean isDeleting = false;

    private TextServicesManager textServicesManager;
    private SpellCheckerSession spellCheckerSession;


    @Override
    public void onCreate() {
        super.onCreate();
        textServicesManager = (TextServicesManager) getSystemService(Context.TEXT_SERVICES_MANAGER_SERVICE);
        if (textServicesManager != null) {
            spellCheckerSession = textServicesManager.newSpellCheckerSession(
                    null, Locale.getDefault(), new SpellCheckerSession.SpellCheckerSessionListener() {
                        @Override
                        public void onGetSuggestions(SuggestionsInfo[] results) {
                            if (results != null && results.length > 0) {
                                List<String> sugestoes = new ArrayList<>();
                                for (int i = 0; i < results[0].getSuggestionsCount(); i++) {
                                    sugestoes.add(results[0].getSuggestionAt(i));
                                }
                                mostrarSugestoes(sugestoes);
                            }
                        }

                        @Override
                        public void onGetSentenceSuggestions(SentenceSuggestionsInfo[] results) {
                            // Deixar por padrão do Google Natural!
                        }
                    }, true
            );
        }
    }

    @Override
    public View onCreateInputView() {
        alphabeticKeyboardView = LayoutInflater.from(this).inflate(R.layout.keyboard_layout, null);
        numericKeyboardView = LayoutInflater.from(this).inflate(R.layout.numeric_layout, null);

        setupAlphabeticKeyboard(alphabeticKeyboardView);
        setupNumericKeyboard(numericKeyboardView);

        return alphabeticKeyboardView;
    }

    private void setupAlphabeticKeyboard(View keyboardView) {
        // Primeira fileira
        setupKey(keyboardView, R.id.key_q, "q");
        setupKey(keyboardView, R.id.key_w, "w");
        setupKeyWithPopup(keyboardView, R.id.key_e, "e", new String[]{"é", "ê", "è"});
        setupKey(keyboardView, R.id.key_r, "r");
        setupKey(keyboardView, R.id.key_t, "t");
        setupKey(keyboardView, R.id.key_y, "y");
        setupKeyWithPopup(keyboardView, R.id.key_u, "u", new String[]{"ú", "û", "ù"});
        setupKeyWithPopup(keyboardView, R.id.key_i, "i", new String[]{"í", "î", "ì"});
        setupKeyWithPopup(keyboardView, R.id.key_o, "o", new String[]{"ó", "ô", "ò", "õ"});
        setupKey(keyboardView, R.id.key_p, "p");

        // Segunda fileira
        setupKeyWithPopup(keyboardView, R.id.key_a, "a", new String[]{"á", "â", "à", "ã"});
        setupKey(keyboardView, R.id.key_s, "s");
        setupKey(keyboardView, R.id.key_d, "d");
        setupKey(keyboardView, R.id.key_f, "f");
        setupKey(keyboardView, R.id.key_g, "g");
        setupKey(keyboardView, R.id.key_h, "h");
        setupKey(keyboardView, R.id.key_j, "j");
        setupKey(keyboardView, R.id.key_k, "k");
        setupKey(keyboardView, R.id.key_l, "l");
        setupKey(keyboardView, R.id.key_ç, "ç");

        // Terceira fileira
        setupKey(keyboardView, R.id.key_z, "z");
        setupKey(keyboardView, R.id.key_x, "x");
        setupKey(keyboardView, R.id.key_c, "c");
        setupKey(keyboardView, R.id.key_v, "v");
        setupKey(keyboardView, R.id.key_b, "b");
        setupKey(keyboardView, R.id.key_n, "n");
        setupKey(keyboardView, R.id.key_m, "m");

        setupKey(keyboardView, R.id.virgula, ",");
        setupKey(keyboardView, R.id.ponto, ".");

        setupDeleteKey(keyboardView);
        setupShiftKey(keyboardView);
        setupNumericKeyButton(keyboardView);
        setupSpaceKey(keyboardView);
        setupDoneKey(keyboardView);
    }

    private void setupNumericKeyboard(View keyboardView) {
        setupKey(keyboardView, R.id.key_0, "0");
        setupKey(keyboardView, R.id.key_1, "1");
        setupKey(keyboardView, R.id.key_2, "2");
        setupKey(keyboardView, R.id.key_3, "3");
        setupKey(keyboardView, R.id.key_4, "4");
        setupKey(keyboardView, R.id.key_5, "5");
        setupKey(keyboardView, R.id.key_6, "6");
        setupKey(keyboardView, R.id.key_7, "7");
        setupKey(keyboardView, R.id.key_8, "8");
        setupKey(keyboardView, R.id.key_9, "9");

        setupKey(keyboardView, R.id.arroba, "@");
        setupKey(keyboardView, R.id.jogodavelha, "#");
        setupKey(keyboardView, R.id.cifrao, "$");
        setupKey(keyboardView, R.id.underline, "_");
        setupKey(keyboardView, R.id.ecomercial, "&");
        setupKey(keyboardView, R.id.menos, "-");
        setupKey(keyboardView, R.id.mais, "+");
        setupKey(keyboardView, R.id.parenteses1, "(");
        setupKey(keyboardView, R.id.parenteses2, ")");
        setupKey(keyboardView, R.id.barra, "/");

        setupKey(keyboardView, R.id.asterisco,"*");
        setupKey(keyboardView, R.id.aspasduplas, "''");
        setupKey(keyboardView, R.id.asplassimples, "'");
        setupKey(keyboardView, R.id.doispontos,":");
        setupKey(keyboardView, R.id.pontoevirgula,";");
        setupKey(keyboardView, R.id.exclamacao,"!");
        setupKey(keyboardView, R.id.interrogacao,"?");

        setupKey(keyboardView, R.id.virgula,",");
        setupKey(keyboardView, R.id.ponto, ".");

        setupDeleteKey(keyboardView);
        setupSpaceKey(keyboardView);
        setupDoneKey(keyboardView);

        Button alphaSwitchButton = keyboardView.findViewById(R.id.alfabeto);
        alphaSwitchButton.setOnClickListener(v -> switchToAlphabeticKeyboard());
    }

    private void setupKeyWithPopup(View keyboardView, int buttonId, String text, String[] popupOptions) {
        Button button = keyboardView.findViewById(buttonId);
        button.setOnClickListener(v -> {
            InputConnection inputConnection = getCurrentInputConnection();
            if (inputConnection != null) {
                String finalText = isShifted ? text.toUpperCase() : text.toLowerCase();
                inputConnection.commitText(finalText, 1);
                verificarOrtografia(finalText);
            }
        });

        button.setOnLongClickListener(v -> {
            showPopupOptions(v, popupOptions);
            return true;
        });
    }

    private void showPopupOptions(View anchorView, String[] options) {
        LinearLayout popupLayout = new LinearLayout(this);
        popupLayout.setOrientation(LinearLayout.HORIZONTAL);

        PopupWindow popupWindow = new PopupWindow(popupLayout, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        for (String option : options) {
            Button optionButton = new Button(this);
            optionButton.setText(option);
            optionButton.setOnClickListener(v -> {
                InputConnection inputConnection = getCurrentInputConnection();
                if (inputConnection != null) {
                    inputConnection.commitText(option, 1);
                }
                popupWindow.dismiss();
            });
            popupLayout.addView(optionButton);
        }

        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(anchorView, 0, -anchorView.getHeight());
    }

    private void setupKey(View keyboardView, int buttonId, String text) {
        Button button = keyboardView.findViewById(buttonId);
        button.setOnClickListener(v -> {
            InputConnection inputConnection = getCurrentInputConnection();
            if (inputConnection != null) {
                String finalText = isShifted ? text.toUpperCase() : text.toLowerCase();
                inputConnection.commitText(finalText, 1);
                verificarOrtografia(finalText);
            }
        });
    }

    private void setupDeleteKey(View keyboardView) {
        Button deleteButton = keyboardView.findViewById(R.id.delete);
        InputConnection inputConnection = getCurrentInputConnection();

        // Inicializa o Handler e o Runnable para deletar continuamente
        deleteHandler = new Handler();
        deleteRunnable = new Runnable() {
            @Override
            public void run() {
                InputConnection inputConnection = getCurrentInputConnection();
                if (inputConnection != null && isDeleting) {
                    inputConnection.deleteSurroundingText(1, 0);
                    deleteHandler.postDelayed(this, 120);
                }
            }
        };


        // Define o OnTouchListener para detectar toque e segurar
        deleteButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Inicia a exclusão contínua
                    isDeleting = true;
                    deleteHandler.post(deleteRunnable);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    // Para a exclusão contínua quando o botão é solto
                    isDeleting = false;
                    deleteHandler.removeCallbacks(deleteRunnable);
                    return true;
            }
            return false;
        });
    }

    private void setupShiftKey(View keyboardView) {
        Button shiftButton = keyboardView.findViewById(R.id.shift);
        shiftButton.setOnClickListener(v -> {
            isShifted = !isShifted;
            updateKeyboard(keyboardView);

            if (isShifted) {
                shiftButton.getBackground().setColorFilter(Color.parseColor("#7E7D7D"), PorterDuff.Mode.MULTIPLY);
            } else {
                shiftButton.getBackground().clearColorFilter();
            }
        });
    }

    private void setupNumericKeyButton(View keyboardView) {
        Button numericButton = keyboardView.findViewById(R.id.numeros);
        numericButton.setOnClickListener(v -> switchToNumericKeyboard());
    }

    private void setupSpaceKey(View keyboardView) {
        Button spaceButton = keyboardView.findViewById(R.id.space);
        spaceButton.setOnClickListener(v -> {
            InputConnection inputConnection = getCurrentInputConnection();
            if (inputConnection != null) {
                inputConnection.commitText(" ", 1);
            }
        });
    }

    private void setupDoneKey(View keyboardView) {
        Button doneButton = keyboardView.findViewById(R.id.done);
        doneButton.setOnClickListener(v -> {
            InputConnection inputConnection = getCurrentInputConnection();
            if (inputConnection != null) {
                inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
            }
        });
    }

    private void switchToNumericKeyboard() {
        setInputView(numericKeyboardView);
        isNumericMode = true;
    }

    private void switchToAlphabeticKeyboard() {
        setInputView(alphabeticKeyboardView);
        isNumericMode = false;
    }

    private void verificarOrtografia(String palavra) {
        if (spellCheckerSession != null && palavra.length() > 2) {
            spellCheckerSession.getSuggestions(new android.view.textservice.TextInfo(palavra), 5);
        }
    }

    @Override
    public void onDestroy() {
        if (spellCheckerSession != null) {
            spellCheckerSession.close();
        }
        super.onDestroy();
    }


    private void mostrarSugestoes(List<String> sugestoes) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sugestões");
        builder.setItems(sugestoes.toArray(new String[0]), (dialog, which) -> {
            InputConnection inputConnection = getCurrentInputConnection();
            if (inputConnection != null) {
                inputConnection.commitText(sugestoes.get(which), 1);
            }
        });
        builder.show();
    }


    public void updateKeyboard(View keyboardView) {
        updateKeyText(keyboardView, R.id.key_q, "Q");
        updateKeyText(keyboardView, R.id.key_w, "W");
        updateKeyWithPopup(keyboardView, R.id.key_e, "E", new String[]{"É", "Ê", "È"});
        updateKeyText(keyboardView, R.id.key_r, "R");
        updateKeyText(keyboardView, R.id.key_t, "T");
        updateKeyText(keyboardView, R.id.key_y, "Y");
        updateKeyWithPopup(keyboardView, R.id.key_u, "U", new String[]{"Ú", "Û", "Ù"});
        updateKeyWithPopup(keyboardView, R.id.key_i, "I", new String[]{"Í", "Î", "Ì"});
        updateKeyWithPopup(keyboardView, R.id.key_o, "O", new String[]{"Ó", "ô", "Ò"});
        updateKeyText(keyboardView, R.id.key_p, "P");

        updateKeyWithPopup(keyboardView, R.id.key_a, "A", new String[]{"Á", "Â", "À"});
        updateKeyText(keyboardView, R.id.key_s, "S");
        updateKeyText(keyboardView, R.id.key_d, "D");
        updateKeyText(keyboardView, R.id.key_f, "F");
        updateKeyText(keyboardView, R.id.key_g, "G");
        updateKeyText(keyboardView, R.id.key_h, "H");
        updateKeyText(keyboardView, R.id.key_j, "J");
        updateKeyText(keyboardView, R.id.key_k, "K");
        updateKeyText(keyboardView, R.id.key_l, "L");
        updateKeyText(keyboardView, R.id.key_ç, "Ç");

        updateKeyText(keyboardView, R.id.key_z, "Z");
        updateKeyText(keyboardView, R.id.key_x, "X");
        updateKeyText(keyboardView, R.id.key_c, "C");
        updateKeyText(keyboardView, R.id.key_v, "V");
        updateKeyText(keyboardView, R.id.key_b, "B");
        updateKeyText(keyboardView, R.id.key_n, "N");
        updateKeyText(keyboardView, R.id.key_m, "M");
    }

    private void updateKeyText(View keyboardView, int buttonId, String text) {
        Button button = keyboardView.findViewById(buttonId);
        if (isShifted) {
            button.setText(text.toUpperCase());
        } else {
            button.setText(text.toLowerCase());
        }
    }

    private void updateKeyWithPopup(View keyboardView, int buttonId, String mainText, String[] popupOptions) {
        Button button = keyboardView.findViewById(buttonId);
        if (isShifted) {
            button.setText(mainText.toUpperCase());
        } else {
            button.setText(mainText.toLowerCase());
        }
    }
}