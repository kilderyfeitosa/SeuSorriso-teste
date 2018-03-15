package com.unicatolica.seusorriso.bd.seusorriso;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class SeuSorrisoDB extends SQLiteOpenHelper {
    public static final String SISMILE_NAME = "sismile.db";
    public static final String USUARIO_TABLE = "usuario";
    public static final int VERSAO = 1;

    public static final String _ID = "_id";
    public static final String MATRICULA = "matricula";
    public static final String NOME = "nome";
    public static final String SENHA = "senha";
    public static final String PERM = "perm";

    public SeuSorrisoDB(Context context){
        super(context, SISMILE_NAME, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE" + USUARIO_TABLE + "(_ID integer primary key autoincrement, MATRICULA text,  NOME text, SENHA text, PERM text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS" + USUARIO_TABLE);
    }

    public String inserirDados(String matricula, String nome, String senha, String perm){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MATRICULA, matricula);
        contentValues.put(NOME, nome);
        contentValues.put(SENHA, senha);
        contentValues.put(PERM, perm);
        long result = db.insert(USUARIO_TABLE, null, contentValues);
        db.close();

        if(result == -1){
            return "teste 1";
        }else{
            return "teste 2";
        }
    }

    public Cursor buscarDados(String tabela){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("Select * from" + tabela, null);
        return res;
    }

    public boolean atualizarDados(String _id, String matricula, String nome, String senha, String perm){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MATRICULA, matricula);
        contentValues.put(NOME, nome);
        contentValues.put(SENHA, senha);
        contentValues.put(PERM, perm);
        int result = db.update(USUARIO_TABLE, contentValues, "_ID=?", new String[]{_id});

        if(result>0){
            return true;
        }else{
            return false;
        }
    }

    public Integer deletarDados(String _id){
        SQLiteDatabase db = this.getWritableDatabase();
        int i = db.delete(USUARIO_TABLE, "_ID=?", new String[]{_id});
        return i;
    }

}
