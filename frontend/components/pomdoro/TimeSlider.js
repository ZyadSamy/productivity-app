import React, { useState } from 'react';
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
  View,
  Slider,
  Flex
} from "native-base";


const TimeSlider = ({onChange, defaultValue, inputText}) => {
    const [value, setValue] = useState(defaultValue)
    return (
            
            <Box style={[{flexDirection:"row", marginBottom:10}]} alignItems="center" w="100%">
            <Text fontFamily={'Impact'}>{inputText}   </Text>
            <Slider maxW="300" defaultValue={defaultValue} minValue={0} maxValue={60} accessibilityLabel="" step={1}
            onChange={v => setValue(v)}
            onChangeEnd={onChange}>
                <Slider.Track>
                <Slider.FilledTrack />
                </Slider.Track>
                <Slider.Thumb />
            </Slider>
            <Text fontFamily={'Impact'}>  {value} min</Text>
            </Box>);
};

export default TimeSlider;