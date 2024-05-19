// Definição da classe árvore B
public class ArvoreB {

  // Ordem da árvore B
  public int ordem;

  // Definição da classe nó da árvore B
  public class No {

    int numChaves;
    long chaves[][] = new long[2 * ordem - 1][2];
    No filhos[] = new No[2 * ordem];
    boolean folha = true;
  }

  // Construtor do nó da árvore B
  public No raiz;

  // Construtor da árvore B
  public ArvoreB(int ordem) {
    this.ordem = ordem;
    raiz = new No();
    raiz.numChaves = 0;
    raiz.folha = true;
  }

  // Função para inserir uma chave na árvore
  public void inserir(long chave1, long chave2) {
    No r = raiz;
    if (r.numChaves == (2 * ordem) - 1) {
      No s = new No();
      raiz = s;
      s.folha = false;
      s.filhos[0] = r;
      s.numChaves = 0;
      splitFilho(s, 0);
      inserirNaoCheio(s, chave1, chave2);
    } else {
      inserirNaoCheio(r, chave1, chave2);
    }
  }

  // Função auxiliar para inserir uma chave em um nó não cheio
  public void inserirNaoCheio(No no, long chave1, long chave2) {
    int i = no.numChaves - 1;
    if (no.folha) {
      while (
        i >= 0 &&
        (
          chave1 < no.chaves[i][0] ||
          (chave1 == no.chaves[i][0] && chave2 < no.chaves[i][1])
        )
      ) {
        no.chaves[i + 1][0] = no.chaves[i][0];
        no.chaves[i + 1][1] = no.chaves[i][1];
        i--;
      }
      no.chaves[i + 1][0] = chave1;
      no.chaves[i + 1][1] = chave2;
      no.numChaves++;
    } else {
      while (
        i >= 0 &&
        (
          chave1 < no.chaves[i][0] ||
          (chave1 == no.chaves[i][0] && chave2 < no.chaves[i][1])
        )
      ) {
        i--;
      }
      i++;
      if (no.filhos[i].numChaves == (2 * ordem) - 1) {
        splitFilho(no, i);
        if (
          chave1 > no.chaves[i][0] ||
          (chave1 == no.chaves[i][0] && chave2 > no.chaves[i][1])
        ) {
          i++;
        }
      }
      inserirNaoCheio(no.filhos[i], chave1, chave2);
    }
  }

  // Função auxiliar para dividir um filho de um nó
  public void splitFilho(No pai, int indiceFilho) {
    No filho = pai.filhos[indiceFilho];
    No novoFilho = new No();
    novoFilho.folha = filho.folha;
    novoFilho.numChaves = ordem - 1;
    for (int j = 0; j < ordem - 1; j++) {
      novoFilho.chaves[j][0] = filho.chaves[j + ordem][0];
      novoFilho.chaves[j][1] = filho.chaves[j + ordem][1];
    }
    if (!filho.folha) {
      for (int j = 0; j < ordem; j++) {
        novoFilho.filhos[j] = filho.filhos[j + ordem];
      }
    }
    filho.numChaves = ordem - 1;
    for (int j = pai.numChaves; j > indiceFilho; j--) {
      pai.filhos[j + 1] = pai.filhos[j];
    }
    pai.filhos[indiceFilho + 1] = novoFilho;
    for (int j = pai.numChaves - 1; j >= indiceFilho; j--) {
      pai.chaves[j + 1][0] = pai.chaves[j][0];
      pai.chaves[j + 1][1] = pai.chaves[j][1];
    }
    pai.chaves[indiceFilho][0] = filho.chaves[ordem - 1][0];
    pai.chaves[indiceFilho][1] = filho.chaves[ordem - 1][1];
    pai.numChaves++;
  }

  // Função para remover uma chave da árvore B
  public void remover(long chave1, long chave2) {
    remover(raiz, chave1, chave2);
  }

  // Função auxiliar para remover uma chave da árvore B
  private void remover(No no, long chave1, long chave2) {
    int indiceChave = encontrarChave(no, chave1, chave2);

    // Caso a chave esteja presente no nó atual
    if (indiceChave != -1) {
      // Caso o nó seja uma folha
      if (no.folha) {
        removerChaveFolha(no, indiceChave);
      } else {
        removerChaveNaoFolha(no, indiceChave);
      }
    } else {
      // Caso a chave não esteja presente no nó atual
      if (no.folha) {
        System.out.println("Chave (" + chave1 + ", " + chave2 + ") não encontrada na árvore.");
        return;
      }

      // Verificar em qual filho a chave pode estar
      int indiceFilho = encontrarIndiceFilho(no, chave1, chave2);

      // Caso o filho não tenha chaves suficientes, preencher o filho
      if (no.filhos[indiceFilho].numChaves < ordem) {
        preencherFilho(no, indiceFilho);
      }

      // Recursivamente remover a chave do filho apropriado
      remover(no.filhos[indiceFilho], chave1, chave2);
    }
  }

  // Função auxiliar para encontrar a chave em um nó
  private int encontrarChave(No no, long chave1, long chave2) {
    for (int i = 0; i < no.numChaves; i++) {
      if (no.chaves[i][0] == chave1 && no.chaves[i][1] == chave2) {
        return i;
      }
    }
    return -1;
  }

  // Função auxiliar para encontrar o índice do filho onde a chave pode estar
  private int encontrarIndiceFilho(No no, long chave1, long chave2) {
    int indiceFilho = 0;
    while (indiceFilho < no.numChaves && (chave1 > no.chaves[indiceFilho][0] || (chave1 == no.chaves[indiceFilho][0] && chave2 > no.chaves[indiceFilho][1]))) {
      indiceFilho++;
    }
    return indiceFilho;
  }

  // Função auxiliar para remover uma chave de um nó folha
  private void removerChaveFolha(No no, int indiceChave) {
    for (int i = indiceChave + 1; i < no.numChaves; i++) {
      no.chaves[i - 1][0] = no.chaves[i][0];
      no.chaves[i - 1][1] = no.chaves[i][1];
    }
    no.numChaves--;
  }

  // Função auxiliar para remover uma chave de um nó não folha
  private void removerChaveNaoFolha(No no, int indiceChave) {
    long chave1 = no.chaves[indiceChave][0];
    long chave2 = no.chaves[indiceChave][1];

    // Caso o filho a esquerda tenha pelo menos ordem chaves
    if (no.filhos[indiceChave].numChaves >= ordem) {
      No predecessor = no.filhos[indiceChave];
      while (!predecessor.folha) {
        predecessor = predecessor.filhos[predecessor.numChaves];
      }
      long predecessorChave1 = predecessor.chaves[predecessor.numChaves - 1][0];
      long predecessorChave2 = predecessor.chaves[predecessor.numChaves - 1][1];
      no.chaves[indiceChave][0] = predecessorChave1;
      no.chaves[indiceChave][1] = predecessorChave2;
      remover(no.filhos[indiceChave], predecessorChave1, predecessorChave2);
    }
    // Caso o filho direito tenha pelo menos ordem chaves
    else if (no.filhos[indiceChave + 1].numChaves >= ordem) {
      No sucessor = no.filhos[indiceChave + 1];
      while (!sucessor.folha) {
        sucessor = sucessor.filhos[0];
      }
      long sucessorChave1 = sucessor.chaves[0][0];
      long sucessorChave2 = sucessor.chaves[0][1];
      no.chaves[indiceChave][0] = sucessorChave1;
      no.chaves[indiceChave][1] = sucessorChave2;
      remover(no.filhos[indiceChave + 1], sucessorChave1, sucessorChave2);
    }
    // Caso ambos os filhos tenham menos que ordem chaves
    else {
      No filhoEsquerdo = no.filhos[indiceChave];
      No filhoDireito = no.filhos[indiceChave + 1];
      filhoEsquerdo.chaves[ordem - 1][0] = chave1;
      filhoEsquerdo.chaves[ordem - 1][1] = chave2;
      for (int i = 0; i < filhoDireito.numChaves; i++) {
        filhoEsquerdo.chaves[i + ordem][0] = filhoDireito.chaves[i][0];
        filhoEsquerdo.chaves[i + ordem][1] = filhoDireito.chaves[i][1];
      }
      for (int i = 0; i < filhoDireito.numChaves + 1; i++) {
        filhoEsquerdo.filhos[i + ordem] = filhoDireito.filhos[i];
      }
      filhoEsquerdo.numChaves += filhoDireito.numChaves + 1;
      for (int i = indiceChave + 1; i < no.numChaves; i++) {
        no.chaves[i - 1][0] = no.chaves[i][0];
        no.chaves[i - 1][1] = no.chaves[i][1];
      }
      for (int i = indiceChave + 2; i <= no.numChaves; i++) {
        no.filhos[i - 1] = no.filhos[i];
      }
      no.numChaves--;
      remover(filhoEsquerdo, chave1, chave2);
    }
  }

  // Função auxiliar para preencher um filho com chaves de outros filhos
  private void preencherFilho(No no, int indiceFilho) {
    // Caso o filho anterior tenha mais de ordem chaves
    if (indiceFilho != 0 && no.filhos[indiceFilho - 1].numChaves >= ordem) {
      emprestarDoAnterior(no, indiceFilho);
    }
    // Caso o próximo filho tenha mais de ordem chaves
    else if (indiceFilho != no.numChaves && no.filhos[indiceFilho + 1].numChaves >= ordem) {
      emprestarDoProximo(no, indiceFilho);
    }
    // Caso ambos os filhos tenham ordem - 1 chaves
    else {
      if (indiceFilho != no.numChaves) {
        fundirFilhos(no, indiceFilho);
      } else {
        fundirFilhos(no, indiceFilho - 1);
      }
    }
  }

  // Função auxiliar para emprestar uma chave do filho anterior
  private void emprestarDoAnterior(No no, int indiceFilho) {
    No filho = no.filhos[indiceFilho];
    No filhoAnterior = no.filhos[indiceFilho - 1];

    for (int i = filho.numChaves - 1; i >= 0; i--) {
      filho.chaves[i + 1][0] = filho.chaves[i][0];
      filho.chaves[i + 1][1] = filho.chaves[i][1];
    }

    if (!filho.folha) {
      for (int i = filho.numChaves; i >= 0; i--) {
        filho.filhos[i + 1] = filho.filhos[i];
      }
    }

    filho.chaves[0][0] = no.chaves[indiceFilho - 1][0];
    filho.chaves[0][1] = no.chaves[indiceFilho - 1][1];

    if (!filho.folha) {
      filho.filhos[0] = filhoAnterior.filhos[filhoAnterior.numChaves];
    }

    no.chaves[indiceFilho - 1][0] = filhoAnterior.chaves[filhoAnterior.numChaves - 1][0];
    no.chaves[indiceFilho - 1][1] = filhoAnterior.chaves[filhoAnterior.numChaves - 1][1];

    filho.numChaves++;
    filhoAnterior.numChaves--;
  }

  // Função auxiliar para emprestar uma chave do próximo filho
  private void emprestarDoProximo(No no, int indiceFilho) {
    No filho = no.filhos[indiceFilho];
    No filhoProximo = no.filhos[indiceFilho + 1];

    filho.chaves[filho.numChaves][0] = no.chaves[indiceFilho][0];
    filho.chaves[filho.numChaves][1] = no.chaves[indiceFilho][1];

    if (!filho.folha) {
      filho.filhos[filho.numChaves + 1] = filhoProximo.filhos[0];
    }

    no.chaves[indiceFilho][0] = filhoProximo.chaves[0][0];
    no.chaves[indiceFilho][1] = filhoProximo.chaves[0][1];

    for (int i = 1; i < filhoProximo.numChaves; i++) {
      filhoProximo.chaves[i - 1][0] = filhoProximo.chaves[i][0];
      filhoProximo.chaves[i - 1][1] = filhoProximo.chaves[i][1];
    }

    if (!filhoProximo.folha) {
      for (int i = 1; i <= filhoProximo.numChaves; i++) {
        filhoProximo.filhos[i - 1] = filhoProximo.filhos[i];
      }
    }

    filho.numChaves++;
    filhoProximo.numChaves--;
  }

  // Função auxiliar para fundir dois filhos em um único filho
  private void fundirFilhos(No no, int indiceFilho) {
    No filho = no.filhos[indiceFilho];
    No filhoProximo = no.filhos[indiceFilho + 1];

    filho.chaves[ordem - 1][0] = no.chaves[indiceFilho][0];
    filho.chaves[ordem - 1][1] = no.chaves[indiceFilho][1];

    for (int i = 0; i < filhoProximo.numChaves; i++) {
      filho.chaves[i + ordem][0] = filhoProximo.chaves[i][0];
      filho.chaves[i + ordem][1] = filhoProximo.chaves[i][1];
    }

    if (!filho.folha) {
      for (int i = 0; i <= filhoProximo.numChaves; i++) {
        filho.filhos[i + ordem] = filhoProximo.filhos[i];
      }
    }

    for (int i = indiceFilho + 1; i < no.numChaves; i++) {
      no.chaves[i - 1][0] = no.chaves[i][0];
      no.chaves[i - 1][1] = no.chaves[i][1];
    }

    for (int i = indiceFilho + 2; i <= no.numChaves; i++) {
      no.filhos[i - 1] = no.filhos[i];
    }

    filho.numChaves += filhoProximo.numChaves + 1;
    no.numChaves--;

    filhoProximo = null;
  }

  public void Mostrar() {
    Mostrar(raiz);
  }

  private void Mostrar(No x) {
    assert (x == null);
    for (int i = 0; i < x.numChaves; i++) {
      System.out.print(
        "(id: " + x.chaves[i][0] + ", posição: " + x.chaves[i][1] + ") "
      );
    }
    if (!x.folha) {
      for (int i = 0; i < x.numChaves + 1; i++) {
        Mostrar(x.filhos[i]);
      }
    }
  }
}
