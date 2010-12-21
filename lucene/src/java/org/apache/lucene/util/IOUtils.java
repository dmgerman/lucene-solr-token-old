begin_unit
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_comment
comment|/** @lucene.internal */
end_comment
begin_class
DECL|class|IOUtils
specifier|public
specifier|final
class|class
name|IOUtils
block|{
DECL|method|IOUtils
specifier|private
name|IOUtils
parameter_list|()
block|{}
comment|// no instance
comment|/**    *<p>Closes all given<tt>Closeable</tt>s, suppressing all thrown exceptions. Some of the<tt>Closeable</tt>s    * may be null, they are ignored. After everything is closed, method either throws<tt>priorException</tt>,    * if one is supplied, or the first of suppressed exceptions, or completes normally.</p>    *<p>Sample usage:<br/>    *<pre>    * Closeable resource1 = null, resource2 = null, resource3 = null;    * ExpectedException priorE = null;    * try {    *   resource1 = ...; resource2 = ...; resource3 = ...; // Aquisition may throw ExpectedException    *   ..do..stuff.. // May throw ExpectedException    * } catch (ExpectedException e) {    *   priorE = e;    * } finally {    *   closeSafely(priorE, resource1, resource2, resource3);    * }    *</pre>    *</p>    * @param priorException<tt>null</tt> or an exception that will be rethrown after method completion    * @param objects         objects to call<tt>close()</tt> on    */
DECL|method|closeSafely
specifier|public
specifier|static
parameter_list|<
name|E
extends|extends
name|Exception
parameter_list|>
name|void
name|closeSafely
parameter_list|(
name|E
name|priorException
parameter_list|,
name|Closeable
modifier|...
name|objects
parameter_list|)
throws|throws
name|E
throws|,
name|IOException
block|{
name|IOException
name|firstIOE
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Closeable
name|object
range|:
name|objects
control|)
block|{
try|try
block|{
if|if
condition|(
name|object
operator|!=
literal|null
condition|)
name|object
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
if|if
condition|(
name|firstIOE
operator|==
literal|null
condition|)
name|firstIOE
operator|=
name|ioe
expr_stmt|;
block|}
block|}
if|if
condition|(
name|priorException
operator|!=
literal|null
condition|)
throw|throw
name|priorException
throw|;
elseif|else
if|if
condition|(
name|firstIOE
operator|!=
literal|null
condition|)
throw|throw
name|firstIOE
throw|;
block|}
comment|/**    *<p>Closes all given<tt>Closeable</tt>s, suppressing all thrown exceptions. Some of the<tt>Closeable</tt>s    * may be null, they are ignored. After everything is closed, method either throws the first of suppressed exceptions,    * or completes normally.</p>    * @param objects         objects to call<tt>close()</tt> on    */
DECL|method|closeSafely
specifier|public
specifier|static
name|void
name|closeSafely
parameter_list|(
name|Closeable
modifier|...
name|objects
parameter_list|)
throws|throws
name|IOException
block|{
name|IOException
name|firstIOE
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Closeable
name|object
range|:
name|objects
control|)
block|{
try|try
block|{
if|if
condition|(
name|object
operator|!=
literal|null
condition|)
name|object
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
if|if
condition|(
name|firstIOE
operator|==
literal|null
condition|)
name|firstIOE
operator|=
name|ioe
expr_stmt|;
block|}
block|}
if|if
condition|(
name|firstIOE
operator|!=
literal|null
condition|)
throw|throw
name|firstIOE
throw|;
block|}
block|}
end_class
end_unit
