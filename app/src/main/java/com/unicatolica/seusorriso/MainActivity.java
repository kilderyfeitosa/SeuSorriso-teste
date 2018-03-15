package com.unicatolica.seusorriso;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.unicatolica.seusorriso.bd.seusorriso.SeuSorrisoDB;

public class MainActivity extends AppCompatActivity {
    SeuSorrisoDB seuSorrisoDB;
    TextView edtMatricula;
    TextView edtSenha;

    String stringMatricula;
    String stringSenha;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtMatricula = (TextView) findViewById(R.id.edtMatricula);
        edtSenha = (TextView) findViewById(R.id.edtSenha);

        stringMatricula = edtMatricula.getText().toString();
        stringSenha = edtSenha.getText().toString();
    }

    public void simular(View view) {
        Cursor res = seuSorrisoDB.buscarDados("usuario");

        if(stringMatricula.isEmpty() && stringSenha.isEmpty()){
            alert("Preencha os campos!");
            edtMatricula.setError("Prencha a Matrícula!");
            edtSenha.setError("Prencha a Senha!");
        }else if(stringMatricula.equals(res.getString(1)) && stringSenha.equals(res.getString(3))){
            Intent intent = new Intent(this, MouthActivity.class);
            intent.putExtra("Matricula",stringMatricula);
            intent.putExtra("Permissao",res.getString(4));

            startActivity(intent);
        }else{
            alert("Login e senha não conferem!");
        }
    }



    public void acessoLivre(View view){
        Intent i = new Intent(this, MouthActivity.class);
        i.putExtra("Matricula","acessoLivre");
        startActivity(i);
    }

    public void alert(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
