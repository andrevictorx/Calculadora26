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

    // atributos a serem manipulados pelos métodos dessa classe
    // relacionado a interface grafica e seu acesso
    private TextView tvVisor; // variável java para acessar o elemento da interface
    private String strVisor;  // string contém o conteúdo a ser mostrado no visor

    // atributos internos do algoritmo da calculadora
    private int estado;  // controle da máquina de estados interna
    private float x, y;  // dois valores envolvidos na conta
    private char op;     // a operacao: + - * / etc

    // Constantes dos estados: dar nome aos números deixa o código legível
    // e evita "magical numbers" espalhados pelos métodos
    private static final int ESPERANDO_NUMERO = 0; // visor mostra 0 ou um resultado
    private static final int DIGITANDO        = 1; // usuário está montando um número
    private static final int ESPERANDO_Y      = 2; // X já foi guardado, aguarda o 2o. número

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

    // =================================================================
    // MÉTODOS AUXILIARES (não são handlers de evento, são de uso interno)
    // =================================================================

    public void resetAC() {
        strVisor = "0"; // valor de reset/inicial para o visor
        tvVisor.setText(strVisor);
        estado = ESPERANDO_NUMERO; // indica que estamos aguardando inicio da digitacao
        x = 0;
        y = 0;
        op = ' '; // nenhuma operacao pendente
    }

    // O visor trabalha com VIRGULA (padrao brasileiro), mas o Java só sabe
    // converter texto em número usando PONTO. Estes dois métodos fazem a
    // tradução nos dois sentidos, num lugar só.
    private float visorParaFloat() {
        return Float.parseFloat(strVisor.replace(',', '.'));
    }

    private void mostraResultado(float valor) {
        String texto;
        if (valor == (long) valor) {
            // resultado é inteiro: mostra "5" em vez do feio "5.0"
            texto = String.valueOf((long) valor);
        } else {
            texto = String.valueOf(valor);
        }
        strVisor = texto.replace('.', ','); // devolve ao padrão brasileiro
        tvVisor.setText(strVisor);
    }

    // =================================================================
    // HANDLERS DE EVENTO (chamados pelo android:onClick do layout)
    // =================================================================

    public void onClickNumero(View view) {
        int id = view.getId(); // id terá a referencia do id da classe R
        String num = "";

        if (id == R.id.b9) {
            num = "9";
        } else if (id == R.id.b8) {
            num = "8";
        } else if (id == R.id.b7) {
            num = "7";
        } else if (id == R.id.b6) {
            num = "6";
        } else if (id == R.id.b5) {
            num = "5";
        } else if (id == R.id.b4) {
            num = "4";
        } else if (id == R.id.b3) {
            num = "3";
        } else if (id == R.id.b2) {
            num = "2";
        } else if (id == R.id.b1) {
            num = "1";
        } else if (id == R.id.b0) {
            num = "0";
        }

        if (estado == ESPERANDO_NUMERO || estado == ESPERANDO_Y) {
            strVisor = num; // numero comecando
        } else if (strVisor.equals("0")) {
            strVisor = num; // evita gerar "007"
        } else {
            strVisor += num; // numero continuando
        }
        tvVisor.setText(strVisor);
        estado = DIGITANDO; // digitacao em andamento
    }

    public void onClickVirgula(View view) {
        if (estado == ESPERANDO_NUMERO || estado == ESPERANDO_Y) {
            strVisor = "0,"; // número novo começando pela parte fracionária
        } else if (!strVisor.contains(",")) {
            strVisor += ","; // só aceita UMA vírgula no mesmo número
        }
        tvVisor.setText(strVisor);
        estado = DIGITANDO;
    }

    // Botão +/- : operação unária que troca o sinal do que está no visor.
    // É feita por manipulação de string, sem mexer na máquina de estados,
    // então funciona tanto durante a digitação quanto sobre um resultado.
    public void onClickSinal(View view) {
        if (strVisor.startsWith("-")) {
            strVisor = strVisor.substring(1); // tira o sinal
        } else if (!strVisor.equals("0")) {
            strVisor = "-" + strVisor;        // põe o sinal
        }
        tvVisor.setText(strVisor);
    }

    public void onClickAC(View view) {
        resetAC();
    }

    public void onClickBackSpace(View view) {
        int tam = strVisor.length(); // comprimento da string
        if (!strVisor.equals("0")) { // é diferente de 0
            if (tam == 1) {
                strVisor = "0"; // apagou o único elemento
                estado = ESPERANDO_NUMERO;
            } else { // tem mais de 1 algarismo
                strVisor = strVisor.substring(0, tam - 1);
                if (strVisor.equals("-")) { // sobrou só o sinal de menos
                    strVisor = "0";
                    estado = ESPERANDO_NUMERO;
                }
            }
        }
        tvVisor.setText(strVisor);
    }

    public void onClickOperacao(View view) {
        int id = view.getId();
        if (id == R.id.bMul) {
            op = '*';
        } else if (id == R.id.bDiv) {
            op = '/';
        } else if (id == R.id.bSoma) {
            op = '+';
        } else if (id == R.id.bSub) {
            op = '-';
        } else if (id == R.id.bPorcento) {
            op = '%'; // FUNCIONALIDADE EXTRA 1 (operação binária)
        }

        estado = ESPERANDO_Y;    // indica que estamos recebendo o 2o. número
        x = visorParaFloat();    // converte e guarda o 1o. número em x
        strVisor = "0";          // valor indicando que vai começar novo número
        tvVisor.setText(strVisor);
    }

    public void onClickIgual(View view) {
        float res;
        y = visorParaFloat(); // converte e guarda o 2o. número em y

        switch (op) {
            case '*':
                res = x * y;
                break;
            case '/':
                if (y == 0) { // proteção contra divisão por zero
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.msg_erro_div_zero),
                            Toast.LENGTH_LONG).show();
                    resetAC();
                    return; // sai do método sem calcular nada
                }
                res = x / y;
                break;
            case '+':
                res = x + y;
                break;
            case '-':
                res = x - y;
                break;
            case '%':
                // FUNCIONALIDADE EXTRA 1 - percentual:  X % Y  =  "Y por cento de X"
                res = x * y / 100;
                break;
            default:
                res = y; // nenhuma operação pendente: o resultado é o próprio visor
                break;
        }

        mostraResultado(res);
        x = res;               // guarda o resultado como possível novo X
        op = ' ';              // a operação já foi consumida
        estado = ESPERANDO_Y;  // próxima digitação começa um número novo
    }

    // FUNCIONALIDADE EXTRA 2 - operação UNÁRIA: aplica direto sobre o visor,
    // sem precisar de um segundo número nem do botão "=".
    public void onClickEspecialUnaria(View view) {
        int id = view.getId();
        float v = visorParaFloat();

        if (id == R.id.bQuadrado) {
            mostraResultado(v * v); // x²
        }

        // O valor já está pronto no visor: a próxima digitação começa um
        // número novo, mas x e op pendentes continuam intactos, então
        // "3 + 4 x² =" resulta corretamente em 19.
        estado = ESPERANDO_Y;
    }
}
