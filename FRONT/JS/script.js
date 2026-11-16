const CORES = ["#e57373", "#64b5f6", "#ffb74d", "#81c784", "#ba68c8"];

function formatarReais(valor) {
  return valor.toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
}

function nomeDaCategoria(categorias, id) {
  for (let i = 0; i < categorias.length; i++) {
    if (categorias[i].id === id) {
      return categorias[i].nome;
    }
  }
  return "-";
}


function mostrarAba(nome) {
  const assinaturas = nome === "assinaturas";
  document.getElementById("telaAssinaturas").hidden = !assinaturas;
  document.getElementById("telaDesejos").hidden = assinaturas;
  document.getElementById("abaAssinaturas").className = assinaturas ? "ativa" : "";
  document.getElementById("abaDesejos").className = assinaturas ? "" : "ativa";
}


function mostrarFormulario(id) {
  const formulario = document.getElementById(id);
  formulario.hidden = !formulario.hidden;
}

function carregar() {
  fetch("http://localhost:8080/api/resumo")
    .then(function (resposta) {
      return resposta.json();
    })
    .then(function (dados) {
      const saldo = dados.limite - dados.totalMensal;

      document.getElementById("saldo").textContent = formatarReais(saldo);
      document.getElementById("saldo").className = dados.estourou ? "negativo" : "";
      document.getElementById("limite").textContent = formatarReais(dados.limite);
      document.getElementById("gasto").textContent = formatarReais(dados.totalMensal);

      const alerta = document.getElementById("alerta");
      if (dados.estourou) {
        alerta.hidden = false;
        alerta.textContent = "ALERTA: você passou do limite em " + formatarReais(-saldo);
      } else {
        alerta.hidden = true;
      }

      
      let opcoes = "";
      for (let i = 0; i < dados.categorias.length; i++) {
        opcoes += "<option value='" + dados.categorias[i].id + "'>" +
                  dados.categorias[i].nome + "</option>";
      }
      document.getElementById("categoria").innerHTML = opcoes;
      document.getElementById("categoriaDesejo").innerHTML = opcoes;

      
      let listaCategorias = "";
      let fatias = [];
      let inicio = 0;

      for (let i = 0; i < dados.categorias.length; i++) {
        let soma = 0;
        for (let j = 0; j < dados.assinaturas.length; j++) {
          if (dados.assinaturas[j].categoriaId === dados.categorias[i].id) {
            soma += dados.assinaturas[j].valorMensal;
          }
        }

        const cor = CORES[i % CORES.length];
        let percentual = 0;
        if (dados.totalMensal > 0) {
          percentual = Math.round(soma / dados.totalMensal * 100);
        }

        listaCategorias +=
          "<li>" +
          "<span class='ponto' style='background:" + cor + "'></span>" +
          dados.categorias[i].nome +
          "<span class='percentual'>" + percentual + "%</span>" +
          "<strong>" + formatarReais(soma) + "</strong>" +
          "</li>";

        if (soma > 0) {
          const fim = inicio + soma / dados.totalMensal * 360;
          fatias.push(cor + " " + inicio + "deg " + fim + "deg");
          inicio = fim;
        }
      }
      document.getElementById("listaCategorias").innerHTML = listaCategorias;

      const rosca = document.getElementById("rosca");
      if (fatias.length > 0) {
        rosca.style.background = "conic-gradient(" + fatias.join(", ") + ")";
      } else {
        rosca.style.background = "#dddddd";
      }

      
      let linhasAssinaturas = "";
      for (let i = 0; i < dados.assinaturas.length; i++) {
        const a = dados.assinaturas[i];
        linhasAssinaturas +=
          "<tr>" +
          "<td>" + a.nome + "</td>" +
          "<td>" + nomeDaCategoria(dados.categorias, a.categoriaId) + "</td>" +
          "<td>" + a.ciclo + ", dia " + a.dia + "</td>" +
          "<td>" + formatarReais(a.valorMensal) + "</td>" +
          "<td><button onclick='remover(" + a.id + ")'>Remover</button></td>" +
          "</tr>";
      }
      document.getElementById("linhasAssinaturas").innerHTML = linhasAssinaturas;

      
      let linhasDesejos = "";
      let totalDesejos = 0;
      for (let i = 0; i < dados.wishlist.length; i++) {
        const item = dados.wishlist[i];
        totalDesejos += item.preco;
        linhasDesejos +=
          "<tr>" +
          "<td>" + item.nome + "</td>" +
          "<td>" + nomeDaCategoria(dados.categorias, item.categoriaId) + "</td>" +
          "<td>" + item.prioridade + "</td>" +
          "<td>" + formatarReais(item.preco) + "</td>" +
          "<td><button onclick='comprar(" + item.id + ")'>Comprei</button></td>" +
          "</tr>";
      }
      document.getElementById("linhasDesejos").innerHTML = linhasDesejos;
      document.getElementById("totalDesejos").textContent = formatarReais(totalDesejos);
    })
    .catch(function () {
      const alerta = document.getElementById("alerta");
      alerta.hidden = false;
      alerta.textContent = "Erro: o Servidor Java está rodando em http://localhost:8080?";
    });
}

function remover(id) {
  fetch("http://localhost:8080/api/desativar?id=" + id, { method: "POST" })
    .then(function (resposta) {
      if (resposta.ok) {
        carregar();
      } else {
        alert("Não foi possível remover.");
      }
    });
}

function comprar(id) {
  fetch("http://localhost:8080/api/comprar?id=" + id, { method: "POST" })
    .then(function (resposta) {
      if (resposta.ok) {
        carregar();
      } else {
        alert("Não foi possível marcar como comprado.");
      }
    });
}

document.getElementById("formAssinatura").addEventListener("submit", function (evento) {
  evento.preventDefault();

  const url = "http://localhost:8080/api/assinaturas" +
    "?nome=" + encodeURIComponent(document.getElementById("nome").value) +
    "&valor=" + document.getElementById("valor").value +
    "&ciclo=" + document.getElementById("ciclo").value +
    "&dia=" + document.getElementById("dia").value +
    "&categoriaId=" + document.getElementById("categoria").value;

  fetch(url, { method: "POST" }).then(function (resposta) {
    if (resposta.ok) {
      document.getElementById("formAssinatura").reset();
      document.getElementById("formAssinatura").hidden = true;
      carregar();
    } else {
      alert("Não foi possível adicionar a assinatura.");
    }
  });
});

document.getElementById("formDesejo").addEventListener("submit", function (evento) {
  evento.preventDefault();

  const url = "http://localhost:8080/api/wishlist" +
    "?nome=" + encodeURIComponent(document.getElementById("nomeDesejo").value) +
    "&preco=" + document.getElementById("precoDesejo").value +
    "&prioridade=" + document.getElementById("prioridadeDesejo").value +
    "&categoriaId=" + document.getElementById("categoriaDesejo").value;

  fetch(url, { method: "POST" }).then(function (resposta) {
    if (resposta.ok) {
      document.getElementById("formDesejo").reset();
      document.getElementById("formDesejo").hidden = true;
      carregar();
    } else {
      alert("Não foi possível adicionar o item.");
    }
  });
});

carregar();
