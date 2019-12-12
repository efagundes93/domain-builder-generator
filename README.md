# domain-builder-generator

Gerar builders manualmente é um pé no saco, pensando nisso este projeto visa auxiliar 
desenvolvedores java que costumam utilizar este design pattern 'automatizando' a geração destas classes.

O **.jar** gerado por este código, espera receber como parâmetro **[0]** o path de onde encontram-se os .class dos dominios
que terão seus builders gerados e **[1]** o path onde encontram-se os **.java** dos dominios gerados.

Basicamente o algoritimo funciona da seguinte forma:

1 - Pressupondo que o padrão de package utilizado é **"com.foo.domain"** e com base no segundo argumento, 
cria-se um sub-diretório **\builder** (onde serão armazenados os **.java** dos builders).

2 - Para cada arquivo .class que estiver no diretório apontado no primeiro argumento,utilizando as bibliotecas [Guava](https://github.com/google/guava) 
e [Byte Code Engineering Library (Apache Commons BCEL™)](https://commons.apache.org/proper/commons-bcel/),
gera-se uma nova classe Builder em um arquivo **.java**.

## Pré-requisitos

-  Java 8
-  Maven decentemente configurado

## Como executar 

1 - Usando o terminal faça clone deste repositório na sua máquina

2 - Execute um ```mvn clean install```

3 - !! passo mais importante !! copie o caminho onde encontram-se os arquivos **.class** dos dominios e o caminho
onde estão os **.java**.

4 - Após execução do build, execute o .jar gerado com os caminhos como argumentos

```java -jar target\domain-builder-generator-0.0.1-SNAPSHOT.jar caminho_dos_.class caminho_dos.java```

Por exemplo:

```java -jar target\domain-builder-generator-0.0.1-SNAPSHOT.jar C:\Java\gambiarra-project\target\classes\br\com\gambiarraproject\domain C:\Java\gambiarra-project\src\main\java\br\com\gambiarraproject\domain```


## Onde vão parar meus builders?
Calma, se não fui claro, eles basicamente estarão em caminho_dos.java/builders ou no caso do exemplo C:\Java\gambiarra-project\src\main\java\br\com\gambiarraproject\domain\builder

## Próximos passos ##

- Melhorar o código
- Transformar este projeto em um plugin do eclipse e/ou maven
