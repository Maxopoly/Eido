<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="" %>
<html>
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="assets/styles.css">
    <script type="text/javascript" src="assets/scripts.js"></script>
    <title>Klausurenverkauf – Fachschaft Mathematik/Informatik</title>
  </head>
  <body>
  <img id="you-obviously-like-owls" src="https://www.fsmi.uni-karlsruhe.de/mediawiki/images/2/23/FS-Eule-KIT.png" alt="">

<h2><s:property value="messageStore.message" /></h2>

  <div id="container" class="container" style="display: none" data-bind="visible: true">
    <div id="cart-save-modal" class="modal fade bs-example-modal-sm" tabindex="-1" role="dialog">
      <div class="modal-dialog modal-sm">
        <div class="modal-content">
          <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
            <h4 class="modal-title">Gespeichert!</h4>
          </div>
          <div class="modal-body">
            Du kannst deine Klausuren und Protokolle nun in der Fachschaft ausdrucken lassen.
          </div>
        </div>
      </div>
    </div>
    <div style="min-height: 700px;">
      <nav class="navbar navbar-default" role="navigation">
        <div class="container-fluid">
          <!-- Brand and toggle get grouped for better mobile display -->
          <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#main-nav">
              <span class="sr-only">Toggle navigation</span>
              <span class="icon-bar"></span>
              <span class="icon-bar"></span>
              <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#">Odie</a>
          </div>

          <div class="collapse navbar-collapse" id="main-nav">
            <ul class="nav navbar-nav">
              <li data-bind="css: { active: activeRoute.startsWith('documentselection') }">
                <a href="#documentselection">
                  Auswahl&nbsp;<span class="badge" data-bind="text: cartSize"></span>
                </a>
              </li>
              <li data-bind="visible: !user.isAuthenticated, css: { active: activeRoute === 'documentsubmission' }"><a href="#documentsubmission">Protokoll einreichen</a>
              </li>
            </ul>
            <ul class="nav navbar-nav navbar-right">
              <li data-bind="visible: user.isAuthenticated, css: { active: activeRoute === 'documentsubmission' }"><a href="#documentsubmission">Dokument erstellen</a>
              <li data-bind="visible: user.isAuthenticated, css: { active: activeRoute === 'internal/preselection' }"><a href="#internal/preselection">Vorauswahl</a></li>
              <li data-bind="visible: user.isAuthenticated, css: { active: activeRoute === 'internal/depositreturn' }"><a href="#internal/depositreturn">Pfandrückgabe</a></li>
              <li data-bind="visible: user.isAuthenticated, css: { active: activeRoute === 'internal/correction' }"><a href="#internal/correction">Abrechnungskorrektur</a></li>
              <li data-bind="visible: user.isAuthenticated"><a data-bind="attr: { href: api.baseUrl + '../admin/' }">DB-Admin</a></li>
              <li class="divider-vertical"></li>
              <li data-bind="visible: !user.isAuthenticated && !store.config.IS_KIOSK"><a data-bind="attr: { href: api.serverOrigin + store.config.LOGIN_PAGE + '?target_paras=&target_path=' + encodeURIComponent(document.location.pathname) }">Als Fachschafter anmelden</a></li>
              <li data-bind="visible: store.config.IS_KIOSK"><a>Kiosk-Modus</a></li>
              <li class="dropdown" data-bind="visible: user.isAuthenticated">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
                  <span style="color: #333333 !important" data-bind="text: 'Hallo,&nbsp;' + user.first_name"></span>
                  <span class="caret"></span>
                </a>
                <ul class="dropdown-menu">
                  <li><a data-bind="attr: { href: api.serverOrigin + store.config.LOGOUT_URL }">Abmelden</a></li>
                </ul>
              </li>
              <li data-bind="visible: user.isAuthenticated">
                <div class="btn-group btn-group-nowrap" role="group" data-bind="with: user">
                  <button class="btn btn-default" type="button" data-bind="css: {active: office === 'FSI'}, click: changeOffice.bind($data, 'FSI')">Info-FS</button>
                  <button class="btn btn-default" type="button" data-bind="css: {active: office === 'FSM'}, click: changeOffice.bind($data, 'FSM')">Mathe-FS</button>
                </div>
              </li>
            </ul>
          </div>

        </div><!-- /.container-fluid -->
      </nav>

      <!-- Yeah yeah, we know. Sorry, Fefe. -->
      <noscript id="nojs" class="text-center">
        <h1 class="banner">:(</h1>
        <h2 class="banner">Leider benötigt diese Seite JavaScript.</h2>
        <p style="font-family: 'Open Sans', arial;">Solltest du JavaScript in deinem Browser nicht anschalten können (oder wollen), kannst du in der Fachschaft vorbeikommen und die Auswahl vor Ort vornehmen.</p>
      </noscript>

      <div data-bind="foreach: api.errors">
        <div class="alert alert-compact alert-danger">
          <button class="close" type="button" data-dismiss="alert">×</button>
          <strong>server says nooooo:</strong> <span data-bind="text: $data"></span>
        </div>
      </div>

      <div data-bind="component: 'log'"></div>

      <!-- content -->
      <div data-bind="page: { id: 'documentselection', role: 'start' }">
        <div data-bind="component: 'documentselection'"></div>
      </div>
      <div data-bind="page: { id: 'documentsubmission' }">
        <div data-bind="component: 'documentsubmission'"></div>
      </div>
      <div data-bind="page: { id: 'datenschutz' }">
        <div data-bind="component: 'datenschutz'"></div>
      </div>
      <div data-bind="page: { id: 'internal', guard: ensureAuthenticated.bind($root) }">
        <div data-bind="page: { id: '?', nameParam: 'pageID' }">
          <div data-bind="component: pageID"></div>
        </div>
      </div>
    </div>

    <!-- footer -->
    <div class="footer">
      <hr>
      <p class="text-center">
      In <a href="http://knockoutjs.com" >Knockout.js</a>, <a href="https://jQuery.com">jQuery</a>, <a href="http://getbootstrap.com">Bootstrap</a> und <a href="http://flask.pocoo.org/">Flask</a> zusammengehackt.<br/>
      Github: <a href="https://github.com/fsmi/odie-client">Frontend</a> | <a href="https://github.com/fsmi/odie-server">Backend</a><br/>
      Sonstige Fragen, Anmerkungen, "Hilfe, alles brennt!"? Schreibe eine Mail an <a href="mailto:odie@fsmi.uni-karlsruhe.de">odie@fsmi.uni-karlsruhe.de</a> | <a href="#datenschutz">Datenschutzerklärung</a>
      </p>
    </div>
  </div>
  </body>
</html>
