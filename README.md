
# Tucil3_13523051_13523108
> Tugas Kecil 3 IF2211 Strategi Algoritma
Penyelesaian Puzzle Rush Hour Menggunakan Algoritma Pathfinding
Semester II Tahun 2024/2025

Program penyelesaian Permainan Puzzle Rush Hour

## Prerequisites

- **Java Development Kit (JDK) 8 atau lebih**  
  https://www.oracle.com/java/technologies/downloads/

## Installation

Cara untuk menjalankan program

1. Clone repository ini dengan perintah:
    ```bash
     https://github.com/henry204xx/Tucil3_13523051_13523108
     ```
2. Jalankan program dengan perintah:
   - Untuk pengguna **Linux**
     ```bash
     make run
     ```
   - Untuk pengguna **Windows**
     ```bash
     .\start.bat
     ```
3. Jika hanya ingin compile program, jalankan:
   - Untuk pengguna **Linux**
     ```bash
     make
     ```
   - Untuk pengguna **Windows**
     ```bash
     .\start.bat all
     ```
4. Untuk input, pastikan input file .txt dengan format: 
    ```bash
    A B
    N
    konfigurasi_papan
    ```
    Contoh: 
    ```bash
    6 6
    11
    AAB..F
    ..BCDF
    GPPCDFK
    GH.III
    GHJ...
    LLJMM.
    ```
   

    | Simbol     | Keterangan                                   |
    |------------|----------------------------------------------|
    | **A , B**   | Dimensi papan A x B (tidak termasuk EXIT)   |
    | **N**      | Jumlah piece selain PRIMARY_PIECE            |
    | **P**      | PRIMARY_PIECE (piece yang ingin dikeluarkan) |
    | **K**      | EXIT                                         |
    | **A-Z**    | Piece selain PRIMARY_PIECE                   |
    | **.**      | Space kosong untuk piece                     |


    
## Features

Program ini dapat
1. Menyelesaikan puzzle Rush Hour dengan ukuran papan AxB dari input file
2. Menyelesaikan puzzle dengan algoritma UCS, BGFS, A*, IDS
3. Menampilkan solusi dalam bentuk animasi
4. Menyimpan solusi dalam bentuk file .txt


## Created by
1. Ferdinand Gabe Tua Sinaga (13523051)  
2. Henry Filberto Shenelo (13523108)
