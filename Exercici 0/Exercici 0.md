<div style="display: flex; width: 100%;">
    <div style="flex: 1; padding: 0px;">
        <p>© Albert Palacios Jiménez, 2024</p>
    </div>
    <div style="flex: 1; padding: 0px; text-align: right;">
        <img src="./assets/ieti.png" height="32" alt="Logo de IETI" style="max-height: 32px;">
    </div>
</div>
<br/>

# Exercici 0

Fes tres perceptrons que classifiquin matrius de 3x3 entre matrius que tenen linies o no tenen linies.

- **El perceptró 0** detecta si hi ha una linia diagonal:

```text
1 0 0          0 0 1
0 1 0          0 1 0
0 0 1          1 0 0
```
- **El perceptró 1** detecta si hi ha una linia vertical:

```text
1 0 0          0 1 0          0 0 1
1 0 0          0 1 0          0 0 1
1 0 0          0 1 0          0 0 1
```

- **El perceptró 2** detecta si hi ha una linia horitzontal:

```text
1 1 1          0 0 0          0 0 0
0 0 0          1 1 1          0 0 0
0 0 0          0 0 0          1 1 1
```

Entrena els perceptrons amb diferents valors **EPOCH**.

Comprova el percentatge d'encert de cada perceptró per diferents entrades i mostra els resultats d'executar l'algorisme amb diferents matrius aleatòries.

- A partir de quants EPOCH cada perceptró té una fiabilitat de més del 50%?
- A partir de quants EPOCH cada perceptró té una fiabilitat de més del 80%?

**Nota**: Fes servir la següent funció per generar totes les combinacions de matrius possibles. 

Veureu que genera matrius d'una dimensió amb nou valors, és a dir:

```text
1 0 1
0 1 0 es representa com: 1 0 1 0 1 0 0 1 0
0 1 0
```

Funció per generar totes les matrius possibles:
```java
public static List<int[]> generateAllMatrices() {
    List<int[]> matrices = new ArrayList<>();
    
    // Hi ha 2^9 combinacions possibles
    for (int i = 0; i < 512; i++) {
        int[] matrix = new int[9];
        
        // Convertir l'índex `i` a una combinació binària de 9 bits
        String binary = String.format("%9s", Integer.toBinaryString(i)).replace(' ', '0');
        
        // Omplir l'array amb els bits corresponents
        for (int j = 0; j < 9; j++) {
            matrix[j] = binary.charAt(j) - '0';
        }
        
        matrices.add(matrix);
    }
    
    return matrices;
}
```

Per tant la següent matriu activa el perceptró "diagonal":

```text
1 0 0
0 1 0 es representa com: 1 0 0 0 1 0 0 0 1
0 0 1
```