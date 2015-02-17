couchbasekafka
==============
Couchbase Agent to push messages to Kafka


#config.properties 
- Contains settings for TAP API and Adapter
cb.cbserver=http://IP:8091/pools - Couchbase server
cb.streamname=cookiestream - TAP specific settings.
cb.startdate=1413500947028
cb.fulldump=true -FULL dump of data from beginning. Doesnt use STREAM name and startdate if fulldump=true

//Used to determine if Transformation required for the message.
enableTransformation=true #If you would like to do preprocessing of data

//Custom class used for transformation.
CBMessageConverter=com.paypal.cookie.utils.CBCookieMessageConverterImpl #Custom class for preprocessing

//Paypal -filtering message based on key - Used by preprocessing class
keyprefixfilter=cs_pp_
analyticskeyprefixfilter=cs_ca_
cs_pp_.topicname=cookie.general
cs_ca_.topicname=cookie.analytics

//Used to switch on/off monitoring
monitoringEnabled=true
sherlockThreshold=200000

#kafkaconfig.properties 
- to configure settings specific to Kafka producer
metadata.broker.list - #Kafka brokers
partitioner.class - Topic partition logic.
request.required.acks- 0, means that the producer never waits for an acknowledgement
cookie.topic #Topic to publish messages to
producer.type=async/sync
//---settings used if async
queue.time
queue.size
batch.size



