package org.zir.dragonieze.dragon;

public class Dragon {
    private long id; // больше 0, уникальным, генерироваться автоматически
    private String name; // не  null, Строка не может быть пустой
    private Coordinates coordinates; // не null
    private java.time.LocalDate creationDate; // не null, генерироваться автоматически
    private DragonCave cave; // не null
    private Person killer; // может быть null
    private Integer age; // больше 0,  может быть null
    private long wingspan; // больше 0
    private Color color; // не null
    private DragonCharacter character; // не null
    private DragonHead head;

}
