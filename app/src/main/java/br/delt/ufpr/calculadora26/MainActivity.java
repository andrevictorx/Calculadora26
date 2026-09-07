package br.delt.ufpr.calculadora26;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    // atributos a serem manipulados pelas métodos dessa classe
    // relacionado a interface grafica e seu acesso
    private TextView tvVisor; // variável java para acessa o elemento da interface
    private String strVisor; // string contém o conteúdo a ser mostrado no visor

    // atributos internos do algoritmo da calculadora
    private int estado; // controle da máquina de estados interna
    private float x, y; // dois valores envolvidos na conta
    private char op; // a operacao: + - * / etc

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // associando os atributos com os elementos da interface
        tvVisor = findViewById(R.id.tvVisor);

        // chamando método para o reset inicial
        resetAC();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void resetAC(){
        strVisor = "0"; // valor de reset/inicial pra o visor
        tvVisor.setText(strVisor);
        estado = 0; // indica que estamos aguardando inicio da digitacao
    }

    public void onClickNumero(View view) {
        int id = view.getId(); // id terá a referencia do id da classe R
        String num = "";

        if (id == R.id.b9){
            num = "9";
        } else if (id == R.id.b8){
            num = "8";
        } else if (id == R.id.b7){
            num = "7";
        }
        // CONTINUAR com o restante dos números

        if (estado == 0 || estado == 2) { // é estado inicial de X ou de Y
            strVisor = num; // numero comecando
        } else {
            strVisor += num; // numero continuando
        }
        tvVisor.setText(strVisor);
        estado = 1; // digitacao do X em andamento
    }

    public void onClickAC(View view) {
        resetAC();
    }

    public void onClickOperacao(View view) {
        int id = view.getId();
        if (id == R.id.bMul) {
            op = '*';
        }
        // CONTINUAR com as demais operacoes

        estado = 2; // indica que estamos recebendo o 2o. número
        x = Float.parseFloat(strVisor); // converte e guarda o 1o. número em x
        strVisor = "0"; // valor indicando que vai começar novo número
        tvVisor.setText(strVisor); // atualizando visor
        Toast.makeText(getApplicationContext(),"Peguei valor X = "+x, Toast.LENGTH_LONG).show();
        // um toast é um aviso que desaparece sozinho ter duraao LONG ou SHOT
        // aqui foi usado para 'debug', confirmar que x está ok
    }

    public void onClickIgual(View view) {
        float res = 0;
        y = Float.parseFloat(strVisor); // converte e guarda o 2o. número em x
        Toast.makeText(getApplicationContext(),"Peguei valor Y = "+y, Toast.LENGTH_LONG).show();
        switch (op) {
            case '*' : res = x * y;
                break;
                // CONTINUAR com as demais operacoes
        }
        strVisor = String.valueOf(res); // transformando resultado float na string do visor
        tvVisor.setText(strVisor);
        x = res; // guarda o resultado como possível X
        estado = 2;
    }

    public void onClickBackSpace(View view) {
        int tam = strVisor.length(); // comprimento da string
        if (!strVisor.equals("0")){ // é diferente de 0
            if (tam==1){
                strVisor = "0"; // apagou o único elemento
                estado = 0;
            } else { // tem mais de 1 algarismo
                strVisor = strVisor.substring(0, tam-1);
            }
        }
        tvVisor.setText(strVisor);
    }
}