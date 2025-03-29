package ru.Andrey;


import lombok.*;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_document")
@Entity
public class AppDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String telegramFileId;

    private String docName;

    @OneToOne
    private BinaryContent binaryContent;//Ссылка на скаченный объект

    private String mimeType;

    private Long fileSize;
}
