import { NoteEditor } from "./NoteEditor";
import React, { useContext, useState, useEffect } from "react";
import { Ionicons } from '@expo/vector-icons'; 
import { Note } from "../UI-Items/Note";

export const respLgFont = {
    base: "20",
    md: "30",
    lg: "35",
}

export const respMdFont = {
  base: "15",
  md: "20",
  lg: "25",
}

export const respSmFont = {
  base: "15",
  md: "18",
  lg: "20",
}

export const respMiniFont = {
  base: "10",
  md: "13",
  lg: "15",
}

export const respLgContHeight = {
  base: 35,
  md: 42,
  lg: 50
}


import {
    Button,
    Text,
    Image,
    Heading,
    HStack,
    Link,
    Box,
    VStack,
    Hidden,
    useToast,
    TextArea,
    Icon,
    Flex,
    Center,
    IconButton,
    FlatList,
  } from "native-base";


  export const CurrentFolderContainer = ({folder, folders}) => {
    // const [isStarred, setIsStarred] = useState(false);


    const [notes, setNotes] = useState([
      {
        id: 1,
        title: `Note 1`,
        content: `bae`,
        date: `24/12/2002`
      },
      {
        id: 2,
        title: `Note 2`,
        content: `ray`,
        date: `25/12/2002`
      },
      {
        id: 3,
        title: `Note 3`,
        content: `jay`,
        date: `26/12/2002`
      },
      {
        id: 4,
        title: `Note 4`,
        content: `gay`,
        date: `27/12/2002`
      }
      
    ])

    const [idCounter, setIdCounter] = useState(notes.length)

    const [selectedNote, setSelectedNote] = useState(null)

    const selectNote = (note) => {
      setSelectedNote(note)
      console.log("Selected Note with ID: ", note.id)
    }

    const editNote = (id, newTitle, newContent) => {
      setNotes(notes.map((note) => note.id === id ? {...note, title: newTitle, content: newContent} : note) )
    }

    const addNote = () => {
      const newCounter = idCounter + 1
      setIdCounter(newCounter)
      const newNote = {id: newCounter, title: ``, content: ``, date: `25/12/2002`}
      setNotes([...notes, newNote])
    }

    const deleteNote = (id) => {
      setNotes(notes.filter((note) => note.id !== id))
    }
    
    return (
      <HStack >
        <VStack 
        flex={{base: 8,md:8, lg:5 }}
        borderColor="black.100"
        borderRightWidth="2"
        >
          <HStack 
            alignContent="center"
            w="full" 
            h={respLgContHeight}
            justifyContent="space-around"
            borderColor="black.100"
            borderBottomWidth="2"
            borderTopWidth="2">
              <Flex ml="3%" mr="auto" justifyContent="center">
                <Text fontSize={respLgFont}>{folder.name}</Text>
              </Flex>
              <VStack justifyContent="center" pr="5%">
              <IconButton 
                onPress={()=> addNote()}
                icon={<Ionicons name="add-circle-sharp" size={30} color="#5BBA59" />} />
              </VStack>
          </HStack>
          <FlatList data={notes} renderItem={({item}) => 
              <Box>
                <Note note={item} onSelect={selectNote}/>
              </Box>} keyExtractor={item => item.id} />
        </VStack>

        {selectedNote!==null && <NoteEditor folders={folders} note = {selectedNote} onEdit={editNote} onDelete={deleteNote}/>}
      </HStack>
        
    );
  };
