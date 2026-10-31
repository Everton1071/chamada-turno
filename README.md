# Chamada do Turno — Usina de Laticínios Jussara

App web para o **turno da noite (23h00 às 07h20)**. Registra as ocorrências de cada turno e gera o relatório mensal **por setor**:

- Acondicionamento
- Sala de envase
- Sala de controle
- Adição
- Recepção de leite
- Serviços gerais
- Prensa
- Caldeira
- Controle de produção

A base de cada mês é o **arquivo de folgas da empresa** (.xlsx).

## Publicar no GitHub Pages

1. Crie um repositório (ex.: `chamada-turno`) e envie estes arquivos para a raiz:
   - `index.html`
   - `manifest.json`
   - `sw.js`
   - `icon-192.png`
   - `icon-512.png`
   - `README.md`
2. No repositório, abra **Settings > Pages**.
3. Em **Source**, escolha **Deploy from a branch**. Selecione a branch `main` e a pasta `/ (root)` e salve.
4. Em 1 a 2 minutos o app fica em `https://SEU-USUARIO.github.io/chamada-turno/`.
5. No celular, abra o link e use **Adicionar à tela inicial** para instalar o app. Depois da primeira abertura com internet, ele também funciona sem conexão.

## Uso no dia a dia

1. **Escolha o setor** tocando no nome no topo da tela.
2. **Carregue a escala do mês:** em **Equipe**, informe o mês e selecione o arquivo de folgas da empresa. Antes de salvar, o app mostra quem é novo e quem saiu, para você informar admissão ou desligamento.
3. **Cadastre as férias:** também em **Equipe**, informe o colaborador, o início e o fim. Quem está de férias não aparece como escalado.
4. **Faça a chamada do turno:** em **Turno** aparecem os escalados da noite. Toque no nome para lançar:
   - Ausência (a definir depois)
   - Falta
   - Atestado
   - Banco de horas
   - Suspensão
   - Treinamento
   - Abono feriado
   - Abono exame médico
5. **Ausências a definir** ficam pendentes em **Lançamentos** até você trocar o tipo. Qualquer lançamento pode ser editado ou excluído depois.
6. **Relatório:** traz o mês do dia 1 ao último dia, com:
   - escalados × presentes;
   - ocorrências por tipo;
   - reincidência e emenda de folga;
   - totais por colaborador.

   Exporte com **Exportar Excel** ou **Imprimir / PDF**.

## Onde ficam os dados

- Os dados ficam **no próprio aparelho**, no navegador. Não existe servidor.
- Cada setor tem os seus dados separados. Se outro setor usar o app no celular dele, os dados ficam só lá.
- Faça um **backup toda semana** em **Ajustes > Baixar backup**. Para trocar de aparelho, use **Restaurar backup** no aparelho novo.
- Limpar os dados do navegador apaga os lançamentos. Por isso o backup é importante.

## Data do turno

O turno é registrado pela **data em que começou**, às 23h. Por exemplo, o turno das 23h00 de 10/10 às 07h20 de 11/10 conta como dia **10/10**. Isso segue a escala de folgas da empresa.
