import React, { useContext, useState } from "react";
import {
  VStack,
  Box,
  FormControl,
  TextArea,
  Button,
  Heading,
  HStack,
  CloseIcon,
  IconButton,
  ScrollView,
} from "native-base";

import { Ionicons } from "@expo/vector-icons";
import { AntDesign } from "@expo/vector-icons";
import FormInput from "../../UI/FormInput";
import SelectableCard from "../../UI/SelectableCard";
import DatePicker from "./DatePicker";
import DaysSelector from "./DaysSelector";
import CategorySelect from "./CategorySelect";
import Toast from "react-native-toast-message";

import AuthContext from "../../store/auth-context";
import { TASK_API } from "../../api/task.api";
import TaskContext from "../../store/task-context";

const AddTaskContent = ({ onAdd, onClose }) => {
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [category, setCategory] = useState(null);
  const [selectedType, setSelectedType] = useState("One time");
  const [dueDate, setDueDate] = useState(null);
  const [repeatOn, setRepeatOn] = useState({});

  const auth = useContext(AuthContext);
  const cxt = useContext(TaskContext);

  const addTask = async () => {
    if (!validateInput()) return;

    let task = {
      taskName: name,
      taskDesc: description,
      category,
    };

    if (selectedType === "One time") {
      task.dueDate = dueDate;
    } else {
      task = { ...task, ...repeatOn };
    }
    // for comptability with backend
    const taskType = selectedType.replaceAll(" ", "").toLowerCase();

    try {
      const id = await TASK_API.createTask(task, taskType, auth.token);
      Toast.show({
        type: "success",
        text1: "Task added successfully.",
      });
      cxt.addTask({ id, ...task });
      console.log(cxt.categories);

      onClose();
    } catch (error) {
      Toast.show({
        type: "error",
        text1: "Error connecting with the server",
        text2: error,
      });
      console.log(error);
    }
  };

  const validateInput = () => {
    if (name.trim().length === 0) {
      Toast.show({
        type: "error",
        text1: "Task name can't be empty",
      });
      return false;
    }

    if (selectedType === "Repeated" && !repeatOn) {
      Toast.show({
        type: "error",
        text1: "You must select atleast 1 day to repeat on.",
      });
      return false;
    }

    return true;
  };

  return (
    <VStack flex={1} w="full" space={4} bgColor="white" p={5} borderRadius="lg">
      <HStack justifyContent={"space-between"} alignItems="center">
        <Heading size="md">Add task</Heading>
        <IconButton
          icon={<CloseIcon size="sm" />}
          _icon={{ color: "black" }}
          onPress={onClose}
        />
      </HStack>
      <ScrollView>
        <VStack space={3}>
          <FormInput
            bgColor="#F8F9FC"
            label={"Name"}
            onChange={setName}
            inputValue={name}
            validationFn={(s) => s.trim().length > 0}
            error={"Task name can't be empty"}
          />
          <FormControl>
            <FormControl.Label>Description</FormControl.Label>
            <TextArea
              bgColor="#F8F9FC"
              onChangeText={(s) => setDescription(s)}
              value={description}
              fontSize="md"
            />
          </FormControl>

          <CategorySelect onSelect={(category) => setCategory(category)} />

          <Box flexDir={"row"} mt={3}>
            <SelectableCard
              label="One time"
              icon={{ package: AntDesign, name: "calendar" }}
              isSelected={selectedType === "One time"}
              setSelected={setSelectedType}
            />
            <SelectableCard
              label="Repeated"
              icon={{ package: Ionicons, name: "ios-repeat" }}
              isSelected={selectedType === "Repeated"}
              setSelected={setSelectedType}
            />
          </Box>

          {selectedType === "One time" ? (
            <FormControl>
              <FormControl.Label>Deadline</FormControl.Label>
              <DatePicker value={dueDate} setValue={setDueDate} />
            </FormControl>
          ) : (
            <DaysSelector onStateChange={(days) => setRepeatOn(days)} />
          )}

          <Button onPress={addTask} mt={5}>
            Add
          </Button>
        </VStack>
      </ScrollView>
    </VStack>
  );
};

export default AddTaskContent;