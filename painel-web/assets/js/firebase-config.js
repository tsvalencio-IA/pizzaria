/*
 * CONFIGURAÇÃO DO PAINEL WEB.
 * Cole aqui os dados do Firebase Web App.
 * O empresaId precisa ser igual ao DEFAULT_EMPRESA_ID do Android Config.kt.
 */
window.firebaseConfig = {
  apiKey: "AIzaSyBtC68h9S3dwB_J8OH8LN3xyda2sqo7PkQ",
  authDomain: "pizza-fa7b5.firebaseapp.com",
  databaseURL: "https://pizza-fa7b5-default-rtdb.firebaseio.com",
  projectId: "pizza-fa7b5",
  storageBucket: "pizza-fa7b5.firebasestorage.app",
  messagingSenderId: "1060341757011",
  appId: "1:1060341757011:web:5fa3f399fb3217a28802a6"
};

window.PIZZARIA_CONFIG = {
  empresaId: "pizzalog",
  nomePizzaria: "Pizzalog",
  telefoneGestor: "",
  enderecoBase: "",

  // Coordenadas da pizzaria. Preencha para o painel calcular distância/retorno.
  baseLat: -20.8126,
  baseLng: -49.3758,

  // Regras comerciais da pizzaria.
  promessaNormalMin: 30,
  alertaSemMotoboyMin: 20,
  promessaEstendidaMin: 40,

  // Motoboy sem atualização acima disso fica como offline/atrasado no painel.
  offlineDepoisDeMin: 3,

  assinatura: "Powered by thIAguinho Soluções Digitais"
};
