package br.com.jussara.chamadaturno;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/** App da Chamada do Turno: abre o app web publicado no GitHub Pages em tela cheia. */
public class MainActivity extends Activity {
    private static final String ENDERECO = "https://everton1071.github.io/chamada-turno/";
    private static final String HOST = "everton1071.github.io";
    private static final int ESCOLHER_ARQUIVO = 10;
    private WebView web;
    private ValueCallback<Uri[]> retornoArquivo;

    @Override
    protected void onCreate(Bundle estado) {
        super.onCreate(estado);
        web = new WebView(this);
        setContentView(web);
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);
        s.setAllowFileAccess(false);
        s.setMediaPlaybackRequiresUserGesture(true);
        web.addJavascriptInterface(new Ponte(), "Android");
        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest req) {
                Uri u = req.getUrl();
                if (HOST.equals(u.getHost())) return false;
                try { startActivity(new Intent(Intent.ACTION_VIEW, u)); } catch (Exception ignored) {}
                return true;
            }
        });
        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView v, ValueCallback<Uri[]> cb, FileChooserParams p) {
                if (retornoArquivo != null) retornoArquivo.onReceiveValue(null);
                retornoArquivo = cb;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                try {
                    startActivityForResult(Intent.createChooser(i, "Escolha o arquivo"), ESCOLHER_ARQUIVO);
                } catch (Exception e) {
                    retornoArquivo = null;
                    return false;
                }
                return true;
            }
        });
        if (estado != null) web.restoreState(estado); else web.loadUrl(ENDERECO);
    }

    @Override
    protected void onActivityResult(int req, int res, Intent dados) {
        if (req == ESCOLHER_ARQUIVO && retornoArquivo != null) {
            Uri[] r = null;
            if (res == RESULT_OK && dados != null && dados.getData() != null) r = new Uri[]{dados.getData()};
            retornoArquivo.onReceiveValue(r);
            retornoArquivo = null;
            return;
        }
        super.onActivityResult(req, res, dados);
    }

    @Override
    protected void onSaveInstanceState(Bundle out) { super.onSaveInstanceState(out); web.saveState(out); }

    @Override
    public void onBackPressed() { if (web.canGoBack()) web.goBack(); else super.onBackPressed(); }

    /** Chamado pela página para salvar arquivos (Excel, backup) e imprimir/gerar PDF. */
    private class Ponte {
        @JavascriptInterface
        public void salvar(String nome, String base64, String tipo) {
            try {
                byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
                Uri uri = gravar(MainActivity.this, nome, tipo, bytes);
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Salvo em Downloads: " + nome, Toast.LENGTH_LONG).show();
                    if (uri != null) {
                        Intent abrir = new Intent(Intent.ACTION_VIEW);
                        abrir.setDataAndType(uri, tipo);
                        abrir.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        try { startActivity(Intent.createChooser(abrir, "Abrir " + nome)); } catch (Exception ignored) {}
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Não foi possível salvar: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }

        @JavascriptInterface
        public void imprimir(String titulo) {
            runOnUiThread(() -> {
                PrintManager pm = (PrintManager) getSystemService(Context.PRINT_SERVICE);
                String nome = (titulo == null || titulo.isEmpty()) ? "Relatorio" : titulo;
                PrintAttributes attrs = new PrintAttributes.Builder()
                        .setMediaSize(PrintAttributes.MediaSize.ISO_A4.asLandscape()).build();
                pm.print(nome, web.createPrintDocumentAdapter(nome), attrs);
            });
        }
    }

    private static Uri gravar(Context ctx, String nome, String tipo, byte[] bytes) throws Exception {
        if (Build.VERSION.SDK_INT >= 29) {
            ContentValues v = new ContentValues();
            v.put(MediaStore.Downloads.DISPLAY_NAME, nome);
            v.put(MediaStore.Downloads.MIME_TYPE, tipo);
            v.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/ChamadaTurno");
            Uri uri = ctx.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, v);
            try (OutputStream os = ctx.getContentResolver().openOutputStream(uri)) { os.write(bytes); }
            return uri;
        } else {
            File dir = ctx.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            File f = new File(dir, nome);
            try (FileOutputStream os = new FileOutputStream(f)) { os.write(bytes); }
            return null;
        }
    }
}
