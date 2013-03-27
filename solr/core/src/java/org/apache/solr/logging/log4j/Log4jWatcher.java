begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.logging.log4j
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|logging
operator|.
name|log4j
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|AppenderSkeleton
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|spi
operator|.
name|LoggingEvent
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|spi
operator|.
name|ThrowableInformation
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrDocument
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|CoreContainer
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|logging
operator|.
name|CircularList
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|logging
operator|.
name|ListenerConfig
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|logging
operator|.
name|LogWatcher
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|logging
operator|.
name|LoggerInfo
import|;
end_import
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Throwables
import|;
end_import
begin_class
DECL|class|Log4jWatcher
specifier|public
class|class
name|Log4jWatcher
extends|extends
name|LogWatcher
argument_list|<
name|LoggingEvent
argument_list|>
block|{
DECL|field|name
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|appender
name|AppenderSkeleton
name|appender
init|=
literal|null
decl_stmt|;
DECL|method|Log4jWatcher
specifier|public
name|Log4jWatcher
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"Log4j ("
operator|+
name|name
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|getAllLevels
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getAllLevels
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
operator|.
name|ALL
operator|.
name|toString
argument_list|()
argument_list|,
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
operator|.
name|TRACE
operator|.
name|toString
argument_list|()
argument_list|,
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
operator|.
name|DEBUG
operator|.
name|toString
argument_list|()
argument_list|,
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
operator|.
name|INFO
operator|.
name|toString
argument_list|()
argument_list|,
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
operator|.
name|WARN
operator|.
name|toString
argument_list|()
argument_list|,
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
operator|.
name|ERROR
operator|.
name|toString
argument_list|()
argument_list|,
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
operator|.
name|FATAL
operator|.
name|toString
argument_list|()
argument_list|,
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
operator|.
name|OFF
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setLogLevel
specifier|public
name|void
name|setLogLevel
parameter_list|(
name|String
name|category
parameter_list|,
name|String
name|level
parameter_list|)
block|{
if|if
condition|(
name|LoggerInfo
operator|.
name|ROOT_NAME
operator|.
name|equals
argument_list|(
name|category
argument_list|)
condition|)
block|{
name|category
operator|=
literal|""
expr_stmt|;
block|}
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
name|log
init|=
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
operator|.
name|getLogger
argument_list|(
name|category
argument_list|)
decl_stmt|;
if|if
condition|(
name|level
operator|==
literal|null
operator|||
literal|"unset"
operator|.
name|equals
argument_list|(
name|level
argument_list|)
operator|||
literal|"null"
operator|.
name|equals
argument_list|(
name|level
argument_list|)
condition|)
block|{
name|log
operator|.
name|setLevel
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|setLevel
argument_list|(
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
operator|.
name|toLevel
argument_list|(
name|level
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getAllLoggers
specifier|public
name|Collection
argument_list|<
name|LoggerInfo
argument_list|>
name|getAllLoggers
parameter_list|()
block|{
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
name|root
init|=
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|LogManager
operator|.
name|getRootLogger
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|LoggerInfo
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|LoggerInfo
argument_list|>
argument_list|()
decl_stmt|;
name|Enumeration
argument_list|<
name|?
argument_list|>
name|loggers
init|=
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|LogManager
operator|.
name|getCurrentLoggers
argument_list|()
decl_stmt|;
while|while
condition|(
name|loggers
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
name|logger
init|=
operator|(
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
operator|)
name|loggers
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|logger
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|logger
operator|==
name|root
condition|)
block|{
continue|continue;
block|}
name|map
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|Log4jInfo
argument_list|(
name|name
argument_list|,
name|logger
argument_list|)
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|dot
init|=
name|name
operator|.
name|lastIndexOf
argument_list|(
literal|"."
argument_list|)
decl_stmt|;
if|if
condition|(
name|dot
operator|<
literal|0
condition|)
break|break;
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|dot
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|map
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|Log4jInfo
argument_list|(
name|name
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|map
operator|.
name|put
argument_list|(
name|LoggerInfo
operator|.
name|ROOT_NAME
argument_list|,
operator|new
name|Log4jInfo
argument_list|(
name|LoggerInfo
operator|.
name|ROOT_NAME
argument_list|,
name|root
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|map
operator|.
name|values
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setThreshold
specifier|public
name|void
name|setThreshold
parameter_list|(
name|String
name|level
parameter_list|)
block|{
if|if
condition|(
name|appender
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Must have an appender"
argument_list|)
throw|;
block|}
name|appender
operator|.
name|setThreshold
argument_list|(
name|Level
operator|.
name|toLevel
argument_list|(
name|level
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getThreshold
specifier|public
name|String
name|getThreshold
parameter_list|()
block|{
if|if
condition|(
name|appender
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Must have an appender"
argument_list|)
throw|;
block|}
return|return
name|appender
operator|.
name|getThreshold
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|registerListener
specifier|public
name|void
name|registerListener
parameter_list|(
name|ListenerConfig
name|cfg
parameter_list|,
name|CoreContainer
name|container
parameter_list|)
block|{
if|if
condition|(
name|history
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"History already registered"
argument_list|)
throw|;
block|}
name|history
operator|=
operator|new
name|CircularList
argument_list|<
name|LoggingEvent
argument_list|>
argument_list|(
name|cfg
operator|.
name|size
argument_list|)
expr_stmt|;
name|appender
operator|=
operator|new
name|EventAppender
argument_list|(
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
name|cfg
operator|.
name|threshold
operator|!=
literal|null
condition|)
block|{
name|appender
operator|.
name|setThreshold
argument_list|(
name|Level
operator|.
name|toLevel
argument_list|(
name|cfg
operator|.
name|threshold
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|appender
operator|.
name|setThreshold
argument_list|(
name|Level
operator|.
name|WARN
argument_list|)
expr_stmt|;
block|}
name|Logger
name|log
init|=
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|LogManager
operator|.
name|getRootLogger
argument_list|()
decl_stmt|;
name|log
operator|.
name|addAppender
argument_list|(
name|appender
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTimestamp
specifier|public
name|long
name|getTimestamp
parameter_list|(
name|LoggingEvent
name|event
parameter_list|)
block|{
return|return
name|event
operator|.
name|timeStamp
return|;
block|}
annotation|@
name|Override
DECL|method|toSolrDocument
specifier|public
name|SolrDocument
name|toSolrDocument
parameter_list|(
name|LoggingEvent
name|event
parameter_list|)
block|{
name|SolrDocument
name|doc
init|=
operator|new
name|SolrDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"time"
argument_list|,
operator|new
name|Date
argument_list|(
name|event
operator|.
name|getTimeStamp
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"level"
argument_list|,
name|event
operator|.
name|getLevel
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"logger"
argument_list|,
name|event
operator|.
name|getLogger
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"message"
argument_list|,
name|event
operator|.
name|getMessage
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ThrowableInformation
name|t
init|=
name|event
operator|.
name|getThrowableInformation
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
name|doc
operator|.
name|setField
argument_list|(
literal|"trace"
argument_list|,
name|Throwables
operator|.
name|getStackTraceAsString
argument_list|(
name|t
operator|.
name|getThrowable
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|doc
return|;
block|}
block|}
end_class
end_unit
