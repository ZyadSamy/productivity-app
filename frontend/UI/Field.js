import {
  Text,
  View,
  StyleSheet,
  TextInput,
  
} from "react-native";
import React from "react";

function Field(props) {
  return (
    <View style={Styles.textBoxContainer}>
      <Text style={Styles.text}>{props.name}</Text>
      <TextInput
        style={Styles.textBox}
        placeholder={`Enter your ${props.name}`}
        placeholderTextColor={"gray"}
        {...props}
      />
    </View>
  );
}

const Styles = StyleSheet.create({
  text: {
    fontSize: 16,
    margin: 3,
  },

  textBoxContainer: {
    backgroundColor: "white",
    marginVertical: "4%",
    borderRadius: 10,
    padding: "2%",
  },

  textBox: {
    paddingHorizontal: "3%",
    fontSize: 16,
    borderWidth: 0,
  },
});

export default Field;
