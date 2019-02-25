package com.example.walther.yugiohdeckapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.Manifest.permission.READ_CONTACTS;

public class Registro extends AppCompatActivity{
    boolean estado;
    EditText nombre, user, pass;
    private List<Usuario> listaUsers;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        listaUsers = new ArrayList<Usuario>();

        nombre = (EditText) findViewById(R.id.et_nombre);
        user = (EditText) findViewById(R.id.et_user);
        pass = (EditText) findViewById(R.id.et_pass);
        estado=false;
        iniciarFireBaseDatabase();
        ObtenerTodosUsuarios();
    }

    private void iniciarFireBaseDatabase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    public void logear(View view){
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }
    public void registrar(View view){
        if(validarDatos()){
            if(compararExistenciaUsuario()){
                //Inicio de Encriptacion basica de Pass +5
                char arrayPass[] = pass.getText().toString().toCharArray();
                for (int i= 0; i<arrayPass.length; i++){
                    arrayPass[i] = (char) (arrayPass[i]+(char)5);
                }
                String passEncriptada = String.valueOf(arrayPass);//Resultado de encriptacion
                Usuario usuario= new Usuario(UUID.randomUUID().toString(), nombre.getText().toString(), user.getText().toString(), passEncriptada);
                databaseReference.child("Usuarios").child(usuario.getId()).setValue(usuario);//Almacenando en la base de Firebase
                Intent intent = new Intent(this, Login.class);
                startActivity(intent);
                UnToast.show(this,"Ahora Inicia Sesión", Toast.LENGTH_SHORT);
            }else{
                UnToast.show(this,"Usuario ya existente", Toast.LENGTH_SHORT);
            }
        }else {
            UnToast.show(this,"Faltan datos", Toast.LENGTH_SHORT);
            if(nombre.getText().toString().equals("")){
                nombre.setError("Requerido");
            }else if(user.getText().toString().equals("")){
                user.setError("Requerido");
            }else if(pass.getText().toString().equals("")){
                pass.setError("Requerido");
            }
        }
    }

    public boolean validarDatos(){
        //Valida que las cajas de texto esten llenas
        boolean estado=false;
        if(nombre.getText().toString().equals("") || user.getText().toString().equals("") || pass.getText().toString().equals("")){
            estado = false;
        }else {
            estado = true;
        }
        return estado;
    }

    public void ObtenerTodosUsuarios() {
        //Obtiene todos los users para luego comparar si ya existe
        databaseReference.child("Usuarios").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaUsers.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Usuario usuario = snapshot.getValue(Usuario.class);
                    listaUsers.add(usuario);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public boolean compararExistenciaUsuario(){
        boolean state = false;
        for(Usuario usuario: listaUsers){
            if(user.getText().toString().equals(usuario.getUsuario())){
                state = false;
            }else{
                state = true;
            }
        }
        return state;
    }
}

