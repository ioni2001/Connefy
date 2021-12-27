package com.example.finalproject.repository.file;

import com.example.finalproject.domain.Entity;
import com.example.finalproject.domain.validators.Validator;
import com.example.finalproject.repository.memory.FriendshipMemoryRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractFriendshipFileRepository<ID, E extends Entity<ID>> extends FriendshipMemoryRepository<ID, E> {
    private String fileName;

    public AbstractFriendshipFileRepository(String fileName, Validator<E> validator) {
        super(validator);
        this.fileName = fileName;
        loadData();
    }

    /**
     * Creates an entity as a string
     * @param entity - an entity
     * @return String - the entity as string created
     * */
    protected abstract String createEntityAsString(E entity);

    /**
     * Creates an entity over a list of strings
     * @param atributes - a list of strings
     * @return E - the entity created
     * */
    protected abstract E extractEntity(List<String> atributes);


    /**
     * Writes an entity to file
     * @param entity - an entity
     * */
    protected void writeToFile(E entity){
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName, true))){
            bufferedWriter.write(createEntityAsString(entity));
            bufferedWriter.newLine();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes a list of entities to file
     * @param friendships- a list of entities
     * */
    protected void writeToFileAll(Iterable<E> friendships){
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName, false))){
            for(E entity:friendships) {
                bufferedWriter.write(createEntityAsString(entity));
                bufferedWriter.newLine();
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read from file, creates the entities and saves them in memory
     * */
    private void loadData(){
        Path path = Paths.get(fileName);
        try{
            List<String> lines = Files.readAllLines(path);
            lines.forEach((line) -> {
                E entity = extractEntity(Arrays.asList(line.split(";")));
                super.save(entity);
            });
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public E save(E entity){
        E e = super.save(entity);

        if(e == null){
            Iterable<E> friendships = super.getAllEntities();
            writeToFileAll(friendships);
        }
        return e;
    }

    @Override
    public E delete(ID id){

        E e = super.delete(id);
        if(e != null){
            Iterable<E> friendships = super.getAllEntities();
            writeToFileAll(friendships);
        }
        return e;
    }

}
