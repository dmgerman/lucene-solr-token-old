begin_unit
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.feeds
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Create documents for the test.  */
end_comment
begin_class
DECL|class|SimpleDocMaker
specifier|public
class|class
name|SimpleDocMaker
extends|extends
name|BasicDocMaker
block|{
DECL|field|docID
specifier|private
name|int
name|docID
init|=
literal|0
decl_stmt|;
DECL|field|DOC_TEXT
specifier|static
specifier|final
name|String
name|DOC_TEXT
init|=
comment|// from a public first aid info at http://firstaid.ie.eu.org
literal|"Well it may be a little dramatic but sometimes it true. "
operator|+
literal|"If you call the emergency medical services to an incident, "
operator|+
literal|"your actions have started the chain of survival. "
operator|+
literal|"You have acted to help someone you may not even know. "
operator|+
literal|"First aid is helping, first aid is making that call, "
operator|+
literal|"putting a Band-Aid on a small wound, controlling bleeding in large "
operator|+
literal|"wounds or providing CPR for a collapsed person whose not breathing "
operator|+
literal|"and heart has stopped beating. You can help yourself, your loved "
operator|+
literal|"ones and the stranger whose life may depend on you being in the "
operator|+
literal|"right place at the right time with the right knowledge."
decl_stmt|;
comment|// return a new docid
DECL|method|newdocid
specifier|private
specifier|synchronized
name|int
name|newdocid
parameter_list|()
block|{
return|return
name|docID
operator|++
return|;
block|}
comment|/*    *  (non-Javadoc)    * @see DocMaker#resetIinputs()    */
DECL|method|resetInputs
specifier|public
specifier|synchronized
name|void
name|resetInputs
parameter_list|()
block|{
name|super
operator|.
name|resetInputs
argument_list|()
expr_stmt|;
name|docID
operator|=
literal|0
expr_stmt|;
block|}
comment|/*    *  (non-Javadoc)    * @see DocMaker#numUniqueTexts()    */
DECL|method|numUniqueTexts
specifier|public
name|int
name|numUniqueTexts
parameter_list|()
block|{
return|return
literal|0
return|;
comment|// not applicable
block|}
DECL|method|getNextDocData
specifier|protected
name|DocData
name|getNextDocData
parameter_list|()
throws|throws
name|NoMoreDataException
block|{
if|if
condition|(
name|docID
operator|>
literal|0
operator|&&
operator|!
name|forever
condition|)
block|{
throw|throw
operator|new
name|NoMoreDataException
argument_list|()
throw|;
block|}
name|addBytes
argument_list|(
name|DOC_TEXT
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|DocData
argument_list|(
literal|"doc"
operator|+
name|newdocid
argument_list|()
argument_list|,
name|DOC_TEXT
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
end_class
end_unit
