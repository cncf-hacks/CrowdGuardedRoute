import * as React from 'react';
import { Modal, View } from 'react-native';
import { Button, Card, Title, Paragraph, Avatar } from 'react-native-paper';

const EmergencyAlert = ({ visible, message, onDismiss }) => {
  const LeftContent = (props) => (
    <Avatar.Icon
      {...props}
      icon="alert"
      color={'white'}
      style={{ backgroundColor: 'red' }}
    />
  );

  return (
    <Modal
      animationType="fade"
      transparent={true}
      visible={visible}
      onRequestClose={onDismiss}
    >
      <Card style={{ backgroundColor: 'red' }}>
        <Card.Title
          title="Emergency Alert"
          subtitle="This is a high priority notification"
          left={LeftContent}
          titleStyle={{ color: 'white' }}
          subtitleStyle={{ color: 'white' }}
        />
        <Card.Content>
          <Title style={{ color: 'white' }}>Title:</Title>
          <Paragraph style={{ color: 'white' }}>{message.title}</Paragraph>
          <Title style={{ color: 'white' }}>Body:</Title>
          <Paragraph style={{ color: 'white' }}>{message.body}</Paragraph>
          <Title style={{ color: 'white' }}>Data:</Title>
          <Paragraph style={{ color: 'white' }}>
            {JSON.stringify(message.data)}
          </Paragraph>
        </Card.Content>
        <Card.Actions>
          <Button mode="contained" onPress={onDismiss}>
            Acknowledge
          </Button>
        </Card.Actions>
      </Card>
    </Modal>
  );
};

export default EmergencyAlert;
