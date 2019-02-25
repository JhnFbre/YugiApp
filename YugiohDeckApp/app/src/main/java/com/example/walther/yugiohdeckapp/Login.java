package com.example.walther.yugiohdeckapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity {
    EditText et_user, et_pass;
    private List<Usuario> listaUsers;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        et_pass = (EditText) findViewById(R.id.et_pass);
        et_user = (EditText) findViewById(R.id.et_user);

        listaUsers = new ArrayList<Usuario>();

        iniciarFireBaseDatabase();
        ObtenerTodosUsuarios();
    }
    private void iniciarFireBaseDatabase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
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
    public void logear(View view){
        if(et_user.getText().toString().equals("") || et_pass.getText().toString().equals("")){
            UnToast.show(this,"Faltan datos", Toast.LENGTH_SHORT);
            if(et_user.getText().toString().equals("")){
                et_user.setError("Requerido");
            }else{
                et_pass.setError("Requerido");
            }
        }else{
            if(compararExistenciaUsuario()){
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }else{
                UnToast.show(this, "Usuario Inválido", Toast.LENGTH_SHORT);
            }
        }
    }
    public void registrar(View view){
        Intent intent = new Intent(this, Registro.class);
        startActivity(intent);
    }
    public boolean compararExistenciaUsuario(){
        boolean state = false;
        String passcifrada = cifrarPassword();
        for(Usuario usuario: listaUsers){
            if(et_user.getText().toString().equals(usuario.getUsuario()) && passcifrada.equals(usuario.getContraseña())){
                state = true;
            }else{
                state =false;
            }
        }
        return state;
    }
    public String cifrarPassword(){
        char arrayPass[] = et_pass.getText().toString().toCharArray();
        for (int i= 0; i<arrayPass.length; i++){
            arrayPass[i] = (char) (arrayPass[i]+(char)5);
        }
        String passEncriptada = String.valueOf(arrayPass);
        return passEncriptada;
    }
}
