begin_unit
begin_package
DECL|package|org.apache.solr
package|package
name|org
operator|.
name|apache
operator|.
name|solr
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|WeakHashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|ConsoleHandler
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Formatter
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Handler
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Level
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|LogRecord
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
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
name|solr
operator|.
name|cloud
operator|.
name|ZkController
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
name|SolrException
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
name|cloud
operator|.
name|Replica
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
name|SolrCore
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
name|request
operator|.
name|SolrQueryRequest
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
name|request
operator|.
name|SolrRequestInfo
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import
begin_class
DECL|class|SolrLogFormatter
specifier|public
class|class
name|SolrLogFormatter
extends|extends
name|Formatter
block|{
comment|/** Add this interface to a thread group and the string returned by    * getTag() will appear in log statements of any threads under that group.    */
DECL|interface|TG
specifier|public
specifier|static
interface|interface
name|TG
block|{
DECL|method|getTag
specifier|public
name|String
name|getTag
parameter_list|()
function_decl|;
block|}
DECL|field|startTime
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
DECL|field|lastTime
name|long
name|lastTime
init|=
name|startTime
decl_stmt|;
DECL|field|methodAlias
name|Map
argument_list|<
name|Method
argument_list|,
name|String
argument_list|>
name|methodAlias
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|class|Method
specifier|public
specifier|static
class|class
name|Method
block|{
DECL|field|className
specifier|public
name|String
name|className
decl_stmt|;
DECL|field|methodName
specifier|public
name|String
name|methodName
decl_stmt|;
DECL|method|Method
specifier|public
name|Method
parameter_list|(
name|String
name|className
parameter_list|,
name|String
name|methodName
parameter_list|)
block|{
name|this
operator|.
name|className
operator|=
name|className
expr_stmt|;
name|this
operator|.
name|methodName
operator|=
name|methodName
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|className
operator|.
name|hashCode
argument_list|()
operator|+
name|methodName
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|obj
operator|instanceof
name|Method
operator|)
condition|)
return|return
literal|false
return|;
name|Method
name|other
init|=
operator|(
name|Method
operator|)
name|obj
decl_stmt|;
return|return
operator|(
name|className
operator|.
name|equals
argument_list|(
name|other
operator|.
name|className
argument_list|)
operator|&&
name|methodName
operator|.
name|equals
argument_list|(
name|other
operator|.
name|methodName
argument_list|)
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|className
operator|+
literal|'.'
operator|+
name|methodName
return|;
block|}
block|}
DECL|method|SolrLogFormatter
specifier|public
name|SolrLogFormatter
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|methodAlias
operator|.
name|put
argument_list|(
operator|new
name|Method
argument_list|(
literal|"org.apache.solr.update.processor.LogUpdateProcessor"
argument_list|,
literal|"finish"
argument_list|)
argument_list|,
literal|"UPDATE"
argument_list|)
expr_stmt|;
name|methodAlias
operator|.
name|put
argument_list|(
operator|new
name|Method
argument_list|(
literal|"org.apache.solr.core.SolrCore"
argument_list|,
literal|"execute"
argument_list|)
argument_list|,
literal|"REQ"
argument_list|)
expr_stmt|;
block|}
comment|// TODO: name this better... it's only for cloud tests where every core container has just one solr server so Port/Core are fine
DECL|field|shorterFormat
specifier|public
name|boolean
name|shorterFormat
init|=
literal|false
decl_stmt|;
comment|/**  Removes info that is redundant for current cloud tests including core name, webapp, and common labels path= and params=    * [] webapp=/solr path=/select params={q=foobarbaz} hits=0 status=0 QTime=1    * /select {q=foobarbaz} hits=0 status=0 QTime=1    * NOTE: this is a work in progress and different settings may be ideal for other types of tests.    */
DECL|method|setShorterFormat
specifier|public
name|void
name|setShorterFormat
parameter_list|()
block|{
name|shorterFormat
operator|=
literal|true
expr_stmt|;
comment|// looking at /update is enough... we don't need "UPDATE /update"
name|methodAlias
operator|.
name|put
argument_list|(
operator|new
name|Method
argument_list|(
literal|"org.apache.solr.update.processor.LogUpdateProcessor"
argument_list|,
literal|"finish"
argument_list|)
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
DECL|class|CoreInfo
specifier|public
specifier|static
class|class
name|CoreInfo
block|{
DECL|field|maxCoreNum
specifier|static
name|int
name|maxCoreNum
decl_stmt|;
DECL|field|shortId
name|String
name|shortId
decl_stmt|;
DECL|field|url
name|String
name|url
decl_stmt|;
DECL|field|coreProps
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|coreProps
decl_stmt|;
block|}
DECL|field|coreInfoMap
name|Map
argument_list|<
name|SolrCore
argument_list|,
name|CoreInfo
argument_list|>
name|coreInfoMap
init|=
operator|new
name|WeakHashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// TODO: use something that survives across a core reload?
DECL|field|classAliases
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|classAliases
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|format
specifier|public
name|String
name|format
parameter_list|(
name|LogRecord
name|record
parameter_list|)
block|{
try|try
block|{
return|return
name|_format
argument_list|(
name|record
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// logging swallows exceptions, so if we hit an exception we need to convert it to a string to see it
return|return
literal|"ERROR IN SolrLogFormatter! original message:"
operator|+
name|record
operator|.
name|getMessage
argument_list|()
operator|+
literal|"\n\tException: "
operator|+
name|SolrException
operator|.
name|toStr
argument_list|(
name|e
argument_list|)
return|;
block|}
block|}
DECL|method|appendThread
specifier|public
name|void
name|appendThread
parameter_list|(
name|StringBuilder
name|sb
parameter_list|,
name|LogRecord
name|record
parameter_list|)
block|{
name|Thread
name|th
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
decl_stmt|;
comment|/******     sb.append(" T=");     sb.append(th.getName()).append(' ');      // NOTE: tried creating a thread group around jetty but we seem to lose it and request     // threads are in the normal "main" thread group     ThreadGroup tg = th.getThreadGroup();     while (tg != null) { sb.append("(group_name=").append(tg.getName()).append(")");        if (tg instanceof TG) {         sb.append(((TG)tg).getTag());         sb.append('/');       }       try {         tg = tg.getParent();       } catch (Throwable e) {         tg = null;       }     }  ******/
comment|// NOTE: LogRecord.getThreadID is *not* equal to Thread.getId()
name|sb
operator|.
name|append
argument_list|(
literal|" T"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|th
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|_format
specifier|public
name|String
name|_format
parameter_list|(
name|LogRecord
name|record
parameter_list|)
block|{
name|String
name|message
init|=
name|record
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|message
operator|.
name|length
argument_list|()
operator|+
literal|80
argument_list|)
decl_stmt|;
name|long
name|now
init|=
name|record
operator|.
name|getMillis
argument_list|()
decl_stmt|;
name|long
name|timeFromStart
init|=
name|now
operator|-
name|startTime
decl_stmt|;
name|long
name|timeSinceLast
init|=
name|now
operator|-
name|lastTime
decl_stmt|;
name|lastTime
operator|=
name|now
expr_stmt|;
name|String
name|shortClassName
init|=
name|getShortClassName
argument_list|(
name|record
operator|.
name|getSourceClassName
argument_list|()
argument_list|,
name|record
operator|.
name|getSourceMethodName
argument_list|()
argument_list|)
decl_stmt|;
comment|/***     sb.append(timeFromStart).append(' ').append(timeSinceLast);     sb.append(' ');     sb.append(record.getSourceClassName()).append('.').append(record.getSourceMethodName());     sb.append(' ');     sb.append(record.getLevel()); ***/
name|SolrRequestInfo
name|requestInfo
init|=
name|SolrRequestInfo
operator|.
name|getRequestInfo
argument_list|()
decl_stmt|;
name|SolrQueryRequest
name|req
init|=
name|requestInfo
operator|==
literal|null
condition|?
literal|null
else|:
name|requestInfo
operator|.
name|getReq
argument_list|()
decl_stmt|;
name|SolrCore
name|core
init|=
name|req
operator|==
literal|null
condition|?
literal|null
else|:
name|req
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|ZkController
name|zkController
init|=
literal|null
decl_stmt|;
name|CoreInfo
name|info
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|core
operator|!=
literal|null
condition|)
block|{
name|info
operator|=
name|coreInfoMap
operator|.
name|get
argument_list|(
name|core
argument_list|)
expr_stmt|;
if|if
condition|(
name|info
operator|==
literal|null
condition|)
block|{
name|info
operator|=
operator|new
name|CoreInfo
argument_list|()
expr_stmt|;
name|info
operator|.
name|shortId
operator|=
literal|"C"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|CoreInfo
operator|.
name|maxCoreNum
operator|++
argument_list|)
expr_stmt|;
name|coreInfoMap
operator|.
name|put
argument_list|(
name|core
argument_list|,
name|info
argument_list|)
expr_stmt|;
if|if
condition|(
name|sb
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|"ASYNC "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" NEW_CORE "
operator|+
name|info
operator|.
name|shortId
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" name="
operator|+
name|core
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" "
operator|+
name|core
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|zkController
operator|==
literal|null
condition|)
block|{
name|zkController
operator|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|getZkController
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|zkController
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|info
operator|.
name|url
operator|==
literal|null
condition|)
block|{
name|info
operator|.
name|url
operator|=
name|zkController
operator|.
name|getBaseUrl
argument_list|()
operator|+
literal|"/"
operator|+
name|core
operator|.
name|getName
argument_list|()
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" url="
operator|+
name|info
operator|.
name|url
operator|+
literal|" node="
operator|+
name|zkController
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|coreProps
init|=
name|getReplicaProps
argument_list|(
name|zkController
argument_list|,
name|core
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|.
name|coreProps
operator|==
literal|null
operator|||
operator|!
name|coreProps
operator|.
name|equals
argument_list|(
name|info
operator|.
name|coreProps
argument_list|)
condition|)
block|{
name|info
operator|.
name|coreProps
operator|=
name|coreProps
expr_stmt|;
specifier|final
name|String
name|corePropsString
init|=
literal|"coll:"
operator|+
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCloudDescriptor
argument_list|()
operator|.
name|getCollectionName
argument_list|()
operator|+
literal|" core:"
operator|+
name|core
operator|.
name|getName
argument_list|()
operator|+
literal|" props:"
operator|+
name|coreProps
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" "
operator|+
name|info
operator|.
name|shortId
operator|+
literal|"_STATE="
operator|+
name|corePropsString
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|sb
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|timeFromStart
argument_list|)
expr_stmt|;
comment|//     sb.append("\nL").append(record.getSequenceNumber());     // log number is useful for sequencing when looking at multiple parts of a log file, but ms since start should be fine.
name|appendThread
argument_list|(
name|sb
argument_list|,
name|record
argument_list|)
expr_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
operator|.
name|append
argument_list|(
name|info
operator|.
name|shortId
argument_list|)
expr_stmt|;
comment|// core
block|}
if|if
condition|(
name|zkController
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" P"
argument_list|)
operator|.
name|append
argument_list|(
name|zkController
operator|.
name|getHostPort
argument_list|()
argument_list|)
expr_stmt|;
comment|// todo: should be able to get this from core container for non zk tests
block|}
if|if
condition|(
name|shortClassName
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
operator|.
name|append
argument_list|(
name|shortClassName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|record
operator|.
name|getLevel
argument_list|()
operator|!=
name|Level
operator|.
name|INFO
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
operator|.
name|append
argument_list|(
name|record
operator|.
name|getLevel
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|appendMultiLineString
argument_list|(
name|sb
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|Throwable
name|th
init|=
name|record
operator|.
name|getThrown
argument_list|()
decl_stmt|;
if|if
condition|(
name|th
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|String
name|err
init|=
name|SolrException
operator|.
name|toStr
argument_list|(
name|th
argument_list|)
decl_stmt|;
name|String
name|ignoredMsg
init|=
name|SolrException
operator|.
name|doIgnore
argument_list|(
name|th
argument_list|,
name|err
argument_list|)
decl_stmt|;
if|if
condition|(
name|ignoredMsg
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|ignoredMsg
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|err
argument_list|)
expr_stmt|;
block|}
block|}
name|sb
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
comment|/*** Isn't core specific... prob better logged from zkController     if (info != null) {       ClusterState clusterState = zkController.getClusterState();       if (info.clusterState != clusterState) {         // something has changed in the matrix...         sb.append(zkController.getBaseUrl() + " sees new ClusterState:");       }     }     ***/
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getReplicaProps
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getReplicaProps
parameter_list|(
name|ZkController
name|zkController
parameter_list|,
name|SolrCore
name|core
parameter_list|)
block|{
specifier|final
name|String
name|collection
init|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCloudDescriptor
argument_list|()
operator|.
name|getCollectionName
argument_list|()
decl_stmt|;
name|Replica
name|replica
init|=
name|zkController
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCachedReplica
argument_list|(
name|collection
argument_list|,
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCloudDescriptor
argument_list|()
operator|.
name|getCoreNodeName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|replica
operator|!=
literal|null
condition|)
block|{
return|return
name|replica
operator|.
name|getProperties
argument_list|()
return|;
block|}
return|return
name|Collections
operator|.
name|EMPTY_MAP
return|;
block|}
DECL|field|classAndMethod
specifier|private
name|Method
name|classAndMethod
init|=
operator|new
name|Method
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// don't need to be thread safe
DECL|method|getShortClassName
specifier|private
name|String
name|getShortClassName
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|method
parameter_list|)
block|{
name|classAndMethod
operator|.
name|className
operator|=
name|name
expr_stmt|;
name|classAndMethod
operator|.
name|methodName
operator|=
name|method
expr_stmt|;
name|String
name|out
init|=
name|methodAlias
operator|.
name|get
argument_list|(
name|classAndMethod
argument_list|)
decl_stmt|;
if|if
condition|(
name|out
operator|!=
literal|null
condition|)
return|return
name|out
return|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|lastDot
init|=
name|name
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
decl_stmt|;
if|if
condition|(
name|lastDot
operator|<
literal|0
condition|)
return|return
name|name
operator|+
literal|'.'
operator|+
name|method
return|;
name|int
name|prevIndex
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|char
name|ch
init|=
name|name
operator|.
name|charAt
argument_list|(
name|prevIndex
operator|+
literal|1
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
name|int
name|idx
init|=
name|name
operator|.
name|indexOf
argument_list|(
literal|'.'
argument_list|,
name|prevIndex
operator|+
literal|1
argument_list|)
decl_stmt|;
name|ch
operator|=
name|name
operator|.
name|charAt
argument_list|(
name|idx
operator|+
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|idx
operator|>=
name|lastDot
operator|||
name|Character
operator|.
name|isUpperCase
argument_list|(
name|ch
argument_list|)
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|name
operator|.
name|substring
argument_list|(
name|idx
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
name|prevIndex
operator|=
name|idx
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
operator|+
literal|'.'
operator|+
name|method
return|;
block|}
DECL|method|addFirstLine
specifier|private
name|void
name|addFirstLine
parameter_list|(
name|StringBuilder
name|sb
parameter_list|,
name|String
name|msg
parameter_list|)
block|{
comment|//    INFO: [] webapp=/solr path=/select params={q=foobarbaz} hits=0 status=0 QTime=1
if|if
condition|(
operator|!
name|shorterFormat
operator|||
operator|!
name|msg
operator|.
name|startsWith
argument_list|(
literal|"["
argument_list|)
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|msg
argument_list|)
expr_stmt|;
return|return;
block|}
name|int
name|idx
init|=
name|msg
operator|.
name|indexOf
argument_list|(
literal|']'
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|<
literal|0
operator|||
operator|!
name|msg
operator|.
name|startsWith
argument_list|(
literal|" webapp="
argument_list|,
name|idx
operator|+
literal|1
argument_list|)
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|msg
argument_list|)
expr_stmt|;
return|return;
block|}
name|idx
operator|=
name|msg
operator|.
name|indexOf
argument_list|(
literal|' '
argument_list|,
name|idx
operator|+
literal|8
argument_list|)
expr_stmt|;
comment|// space after webapp=
if|if
condition|(
name|idx
operator|<
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|msg
argument_list|)
expr_stmt|;
return|return;
block|}
name|idx
operator|=
name|msg
operator|.
name|indexOf
argument_list|(
literal|'='
argument_list|,
name|idx
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|// = in  path=
if|if
condition|(
name|idx
operator|<
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|msg
argument_list|)
expr_stmt|;
return|return;
block|}
name|int
name|idx2
init|=
name|msg
operator|.
name|indexOf
argument_list|(
literal|' '
argument_list|,
name|idx
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx2
operator|<
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|msg
argument_list|)
expr_stmt|;
return|return;
block|}
name|sb
operator|.
name|append
argument_list|(
name|msg
operator|.
name|substring
argument_list|(
name|idx
operator|+
literal|1
argument_list|,
name|idx2
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// path
name|idx
operator|=
name|msg
operator|.
name|indexOf
argument_list|(
literal|"params="
argument_list|,
name|idx2
argument_list|)
expr_stmt|;
if|if
condition|(
name|idx
operator|<
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|msg
operator|.
name|substring
argument_list|(
name|idx2
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|msg
operator|.
name|substring
argument_list|(
name|idx
operator|+
literal|7
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|appendMultiLineString
specifier|private
name|void
name|appendMultiLineString
parameter_list|(
name|StringBuilder
name|sb
parameter_list|,
name|String
name|msg
parameter_list|)
block|{
name|int
name|idx
init|=
name|msg
operator|.
name|indexOf
argument_list|(
literal|'\n'
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|<
literal|0
condition|)
block|{
name|addFirstLine
argument_list|(
name|sb
argument_list|,
name|msg
argument_list|)
expr_stmt|;
return|return;
block|}
name|int
name|lastIdx
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
if|if
condition|(
name|idx
operator|<
literal|0
condition|)
block|{
if|if
condition|(
name|lastIdx
operator|==
operator|-
literal|1
condition|)
block|{
name|addFirstLine
argument_list|(
name|sb
argument_list|,
name|msg
operator|.
name|substring
argument_list|(
name|lastIdx
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|msg
operator|.
name|substring
argument_list|(
name|lastIdx
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
if|if
condition|(
name|lastIdx
operator|==
operator|-
literal|1
condition|)
block|{
name|addFirstLine
argument_list|(
name|sb
argument_list|,
name|msg
operator|.
name|substring
argument_list|(
name|lastIdx
operator|+
literal|1
argument_list|,
name|idx
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|msg
operator|.
name|substring
argument_list|(
name|lastIdx
operator|+
literal|1
argument_list|,
name|idx
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"\n\t"
argument_list|)
expr_stmt|;
name|lastIdx
operator|=
name|idx
expr_stmt|;
name|idx
operator|=
name|msg
operator|.
name|indexOf
argument_list|(
literal|'\n'
argument_list|,
name|lastIdx
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getHead
specifier|public
name|String
name|getHead
parameter_list|(
name|Handler
name|h
parameter_list|)
block|{
return|return
name|super
operator|.
name|getHead
argument_list|(
name|h
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getTail
specifier|public
name|String
name|getTail
parameter_list|(
name|Handler
name|h
parameter_list|)
block|{
return|return
name|super
operator|.
name|getTail
argument_list|(
name|h
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|formatMessage
specifier|public
name|String
name|formatMessage
parameter_list|(
name|LogRecord
name|record
parameter_list|)
block|{
return|return
name|format
argument_list|(
name|record
argument_list|)
return|;
block|}
DECL|field|threadLocal
specifier|static
name|ThreadLocal
argument_list|<
name|String
argument_list|>
name|threadLocal
init|=
operator|new
name|ThreadLocal
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|Handler
index|[]
name|handlers
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
literal|""
argument_list|)
operator|.
name|getHandlers
argument_list|()
decl_stmt|;
name|boolean
name|foundConsoleHandler
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|index
init|=
literal|0
init|;
name|index
operator|<
name|handlers
operator|.
name|length
condition|;
name|index
operator|++
control|)
block|{
comment|// set console handler to SEVERE
if|if
condition|(
name|handlers
index|[
name|index
index|]
operator|instanceof
name|ConsoleHandler
condition|)
block|{
name|handlers
index|[
name|index
index|]
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
name|handlers
index|[
name|index
index|]
operator|.
name|setFormatter
argument_list|(
operator|new
name|SolrLogFormatter
argument_list|()
argument_list|)
expr_stmt|;
name|foundConsoleHandler
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|foundConsoleHandler
condition|)
block|{
comment|// no console handler found
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"No consoleHandler found, adding one."
argument_list|)
expr_stmt|;
name|ConsoleHandler
name|consoleHandler
init|=
operator|new
name|ConsoleHandler
argument_list|()
decl_stmt|;
name|consoleHandler
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
name|consoleHandler
operator|.
name|setFormatter
argument_list|(
operator|new
name|SolrLogFormatter
argument_list|()
argument_list|)
expr_stmt|;
name|Logger
operator|.
name|getLogger
argument_list|(
literal|""
argument_list|)
operator|.
name|addHandler
argument_list|(
name|consoleHandler
argument_list|)
expr_stmt|;
block|}
specifier|final
name|org
operator|.
name|slf4j
operator|.
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SolrLogFormatter
operator|.
name|class
argument_list|)
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
literal|"HELLO"
argument_list|)
expr_stmt|;
name|ThreadGroup
name|tg
init|=
operator|new
name|MyThreadGroup
argument_list|(
literal|"YCS"
argument_list|)
decl_stmt|;
name|Thread
name|th
init|=
operator|new
name|Thread
argument_list|(
name|tg
argument_list|,
literal|"NEW_THREAD"
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|go
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|th
operator|.
name|start
argument_list|()
expr_stmt|;
name|th
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
DECL|class|MyThreadGroup
specifier|static
class|class
name|MyThreadGroup
extends|extends
name|ThreadGroup
implements|implements
name|TG
block|{
DECL|method|MyThreadGroup
specifier|public
name|MyThreadGroup
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTag
specifier|public
name|String
name|getTag
parameter_list|()
block|{
return|return
literal|"HELLO"
return|;
block|}
block|}
DECL|method|go
specifier|public
specifier|static
name|void
name|go
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|org
operator|.
name|slf4j
operator|.
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SolrLogFormatter
operator|.
name|class
argument_list|)
decl_stmt|;
name|Thread
name|thread1
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|threadLocal
operator|.
name|set
argument_list|(
literal|"from thread1"
argument_list|)
expr_stmt|;
name|log
operator|.
name|error
argument_list|(
literal|"[] webapp=/solr path=/select params={hello} wow"
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|Thread
name|thread2
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|threadLocal
operator|.
name|set
argument_list|(
literal|"from thread2"
argument_list|)
expr_stmt|;
name|log
operator|.
name|error
argument_list|(
literal|"InThread2"
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|thread1
operator|.
name|start
argument_list|()
expr_stmt|;
name|thread2
operator|.
name|start
argument_list|()
expr_stmt|;
name|thread1
operator|.
name|join
argument_list|()
expr_stmt|;
name|thread2
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
