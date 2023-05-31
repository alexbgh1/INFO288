#include <mpi.h>
#include <vector>
#include <iostream>
#include <fstream>
#include <stdio.h>
using namespace std;

void printVector(vector<int> nums, int rank){
    cout << "P" << rank << " => ";
    for(int i=0; i<nums.size();i++){
                cout << nums[i] << " ";
    } cout << endl;
}

int mayorEnVector(vector<int> nums){
    int mayor = nums[0];
    for (int i = 1; i < nums.size(); i++){
        if (nums[i] > mayor){
            mayor = nums[i];
        }
    }
    return mayor;
}

int main(int argc, char** argv) {
    // Definimos:
    // rank: identificador del proceso
    // size: cantidad de procesos
    int rank, size;

    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &size); 
    MPI_Comm_rank(MPI_COMM_WORLD, &rank); 

    // - - - PASO 1: Validaciones - - -
    if (argc != 1){
        if (rank == 0) {
            cout << "Error" << endl;
            cout << "Ejemplo de ingreso de ejecucion: mpirun -np 4 ./prog" << endl;
        }
        MPI_Finalize();
        return 0;
    }

    if (size != 4){
        if (rank == 0) {
            cout << "Error" << endl;
            cout << "Ejemplo de ingreso de ejecucion: mpirun -np 4 ./prog" << endl;
        }
        MPI_Finalize();
        return 0;
    }

    // - - - PASO 2: Leer archivo - - -

    vector<int> nums;
    if(rank == 0){
        ifstream file("vector.txt");
        if (!file.is_open()){
            cout << "Error" << endl;
            cout << "No se pudo abrir el archivo." << endl;
            // MPI_Abort(comm, errorcode)
            MPI_Abort(MPI_COMM_WORLD, 1); // Terminar todos los procesos
        }

        // Leemos el archivo separado por punto y comas
        string line;
        while (getline(file, line, ';')){
            nums.push_back(stoi(line));
        }
        file.close();

        // - - - PASO 3: El archivo debe ser multiplo de 4 y mayor a 4 - - -
        if (nums.size() % 4 != 0 || nums.size() == 4){
            cout << "Error" << endl;
            cout << "El archivo debe ser multiplo de 4 y mayor a 4." << endl;
            MPI_Abort(MPI_COMM_WORLD, 1); // Terminar todos los procesos
        }

        cout << "Archivo leido correctamente." << endl;
        cout << "Vector: ";
        for (int i = 0; i < nums.size(); i++){
            cout << nums[i] << " ";
        } cout << endl;

        // - - - PASO 4: Dividimos el vector en 4 partes de largo igual, cada una debe ir a un core distinto - - -

        // Calculamos el largo de cada parte del vector
        int largo = nums.size() / 4;

        // Enviamos el largo de cada parte a cada core
        for (int i = 1; i < 4; i++){
            // MPI_Send(&buffer, count, datatype, dest, tag, comm)
            // buffer: vector a enviar (tamaño)
            // count: cantidad de elementos a enviar
            // datatype: tipo de dato a enviar
            // dest: destino del mensaje (rank)
            // tag: identificador del mensaje
            // comm: comunicador
            MPI_Send(&largo, 1, MPI_INT, i, 0, MPI_COMM_WORLD);
        }

        // Enviamos cada parte del vector a cada core
        for (int i = 1; i < 4; i++){
            // MPI_Send(&buffer, count, datatype, dest, tag, comm)
            // buffer: vector a enviar (tamaño) -> &nums[i*largo] = 
            MPI_Send(&nums[i*largo], largo, MPI_INT, i, 0, MPI_COMM_WORLD);
        }

        cout << "Valores enviados a los cores." << endl;

        // - - - PASO 5: Recibimos los datos en cada core - - -
        // Asignamos la primera parte del vector al core 0
        nums = vector<int>(nums.begin(), nums.begin() + largo);

        printVector(nums, rank);

        // - - - PASO 6: Cada core calcula el mayor de su parte del vector - - -
        // Imprimir mayor de cada parte del vector
        int mayor = mayorEnVector(nums);
        cout << "mayor en p" << rank << ": " << mayor << endl;

        // - - - PASO 7: Cada core envia su mayor al core 0 - - -

        // Core 0 recibe
        int mayor1, mayor2, mayor3;
        MPI_Recv(&mayor1, 1, MPI_INT, 1, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
        MPI_Recv(&mayor2, 1, MPI_INT, 2, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
        MPI_Recv(&mayor3, 1, MPI_INT, 3, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);

        // Calcula la suma de los mayores
        int suma = mayor + mayor1 + mayor2 + mayor3;
        // Imprime la suma de los mayores
        cout << "suma total = " << suma << endl;

        MPI_Finalize();
        return 0;
    }

    // - - - PASO 5: Recibimos los datos en cada core - - -

    // Recibimos el largo de cada parte del vector
    int largo;
    MPI_Recv(&largo, 1, MPI_INT, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);

    // Recibimos cada parte del vector
    vector<int> nums1(largo);
    MPI_Recv(&nums1[0], largo, MPI_INT, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);

    // PRINT : P{num} => 2 4 6
    printVector(nums1, rank);

    // - - - PASO 6: Cada core calcula el mayor de su parte del vector - - -
    // Imprimir mayor de cada parte del vector
    int mayor = mayorEnVector(nums1);
    cout << "mayor en p" << rank << ": " << mayor << endl;

    // - - - PASO 7: Cada core envia su mayor al core 0 - - -
    // Enviamos el mayor de cada parte del vector al core 0
    MPI_Send(&mayor, 1, MPI_INT, 0, 0, MPI_COMM_WORLD);

    // Finalize the MPI environment.
    MPI_Finalize();
    return 0;
}

