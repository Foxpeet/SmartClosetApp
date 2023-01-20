package com.example.grupo1_1.smartclosset.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.grupo1_1.smartclosset.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CambiarContrasenya extends AppCompatDialogFragment {
    AlertDialog dialog;
    FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
    public EditText contrasenyaAntigua;
    public EditText contrasenyaNueva;
    public EditText confirmarContrasenya;

    public ImageView ojoContrasenyaAntigua;
    public ImageView ojoContrasenyaNueva;
    public ImageView ojoContrasenyaConfirmar;

    boolean showPasswordAntiguo;
    boolean showPasswordNuevo;
    boolean showPasswordConfirmar;

    String correoActual;
    TextView err1;
    TextView err2;
    TextView err3;
    TextView err4;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view =inflater.inflate(R.layout.activity_cambiar_contrasenya, null);

        dialog = builder.setView(view)
                .setTitle(getResources().getString(R.string.cambiaContrasenya_titulo))
                .setNegativeButton(getResources().getString(R.string.Comunes_cancelar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                })
                .setPositiveButton(getResources().getString(R.string.Comunes_guardar), null)
                .create();

        dialog.setCancelable(false);

        correoActual = usuario.getEmail();

        contrasenyaAntigua = view.findViewById(R.id.antiguaContrasenyaPerfil);
        contrasenyaNueva = view.findViewById(R.id.nuevaContrasenyaPerfil);
        confirmarContrasenya = view.findViewById(R.id.confirmarContrasenyaPerfil);

        ojoContrasenyaAntigua = view.findViewById(R.id.imageViewOjoContraseña1);
        ojoContrasenyaNueva = view.findViewById(R.id.imageViewOjoContraseña2);
        ojoContrasenyaConfirmar = view.findViewById(R.id.imageViewOjoContraseña3);

        err1 = view.findViewById(R.id.textoErr6);
        err1.setVisibility(View.INVISIBLE);

        err2 = view.findViewById(R.id.textoErr7);
        err2.setVisibility(View.INVISIBLE);

        err3 = view.findViewById(R.id.textoErr8);
        err3.setVisibility(View.INVISIBLE);

        err4 = view.findViewById(R.id.textoErr9);
        err4.setVisibility(View.INVISIBLE);

        showPasswordAntiguo = false;
        showPasswordNuevo = false;
        showPasswordConfirmar = false;

        ojoContrasenyaAntigua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int inicioCursor = contrasenyaAntigua.getSelectionStart();
                int finalCursor = contrasenyaAntigua.getSelectionStart();
                if(showPasswordAntiguo){
                    contrasenyaAntigua.setTransformationMethod(new PasswordTransformationMethod());
                    showPasswordAntiguo = false;
                    ojoContrasenyaAntigua.setImageDrawable(getResources().getDrawable(R.drawable.icono_ojo_hide));
                    contrasenyaAntigua.setSelection(inicioCursor, finalCursor);
                } else{
                    contrasenyaAntigua.setTransformationMethod(null);
                    showPasswordAntiguo = true;
                    ojoContrasenyaAntigua.setImageDrawable(getResources().getDrawable(R.drawable.icono_ojo_show));
                    contrasenyaAntigua.setSelection(inicioCursor, finalCursor);
                }
            }
        });

        ojoContrasenyaNueva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int inicioCursor = contrasenyaNueva.getSelectionStart();
                int finalCursor = contrasenyaNueva.getSelectionStart();
                if(showPasswordNuevo){
                    contrasenyaNueva.setTransformationMethod(new PasswordTransformationMethod());
                    showPasswordNuevo = false;
                    ojoContrasenyaNueva.setImageDrawable(getResources().getDrawable(R.drawable.icono_ojo_hide));
                    contrasenyaNueva.setSelection(inicioCursor, finalCursor);
                } else{
                    contrasenyaNueva.setTransformationMethod(null);
                    showPasswordNuevo = true;
                    ojoContrasenyaNueva.setImageDrawable(getResources().getDrawable(R.drawable.icono_ojo_show));
                    contrasenyaNueva.setSelection(inicioCursor, finalCursor);
                }
            }
        });

        ojoContrasenyaConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int inicioCursor = confirmarContrasenya.getSelectionStart();
                int finalCursor = confirmarContrasenya.getSelectionStart();
                if(showPasswordConfirmar){
                    confirmarContrasenya.setTransformationMethod(new PasswordTransformationMethod());
                    showPasswordConfirmar = false;
                    ojoContrasenyaConfirmar.setImageDrawable(getResources().getDrawable(R.drawable.icono_ojo_hide));
                    confirmarContrasenya.setSelection(inicioCursor, finalCursor);
                } else{
                    confirmarContrasenya.setTransformationMethod(null);
                    showPasswordConfirmar = true;
                    ojoContrasenyaConfirmar.setImageDrawable(getResources().getDrawable(R.drawable.icono_ojo_show));
                    confirmarContrasenya.setSelection(inicioCursor, finalCursor);
                }
            }
        });

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        comprobarContrasenyas();
                    }
                });
                Button buttonCancel = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                buttonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });
        return dialog;
    }

    public void comprobarContrasenyas(){
        if(!contrasenyaAntigua.getText().toString().isEmpty() && !contrasenyaNueva.getText().toString().isEmpty() && !confirmarContrasenya.getText().toString().isEmpty()){
            err1.setVisibility(View.VISIBLE);
            AuthCredential credential = EmailAuthProvider.getCredential(correoActual, contrasenyaAntigua.getText().toString());
            if(contrasenyaNueva.getText().toString().equals(confirmarContrasenya.getText().toString())){
                err2.setVisibility(View.INVISIBLE);
                usuario.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    err4.setVisibility(View.INVISIBLE);
                                    usuario.updatePassword(contrasenyaNueva.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getActivity(), getResources().getString(R.string.cambiaContrasenya_exito), Toast.LENGTH_LONG).show();
                                                err3.setVisibility(View.INVISIBLE);
                                                dialog.dismiss();
                                            } else {
                                                err3.setVisibility(View.VISIBLE);
                                                dialog.dismiss();
                                            }
                                        }
                                    });
                                } else {
                                    err4.setVisibility(View.VISIBLE);
                                }
                            }
                        });
            } else{
                err2.setVisibility(View.VISIBLE);
            }
        }else{
            err1.setVisibility(View.VISIBLE);
        }
    }
}