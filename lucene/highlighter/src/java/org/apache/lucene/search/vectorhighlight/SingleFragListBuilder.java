begin_unit
begin_package
DECL|package|org.apache.lucene.search.vectorhighlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|vectorhighlight
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
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|vectorhighlight
operator|.
name|FieldFragList
operator|.
name|WeightedFragInfo
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|vectorhighlight
operator|.
name|FieldPhraseList
operator|.
name|WeightedPhraseInfo
import|;
end_import
begin_comment
comment|/**  * An implementation class of {@link FragListBuilder} that generates one {@link WeightedFragInfo} object.  * Typical use case of this class is that you can get an entire field contents  * by using both of this class and {@link SimpleFragmentsBuilder}.<br/>  *<pre class="prettyprint">  * FastVectorHighlighter h = new FastVectorHighlighter( true, true,  *   new SingleFragListBuilder(), new SimpleFragmentsBuilder() );  *</pre>  */
end_comment
begin_class
DECL|class|SingleFragListBuilder
specifier|public
class|class
name|SingleFragListBuilder
implements|implements
name|FragListBuilder
block|{
DECL|method|createFieldFragList
specifier|public
name|FieldFragList
name|createFieldFragList
parameter_list|(
name|FieldPhraseList
name|fieldPhraseList
parameter_list|,
name|int
name|fragCharSize
parameter_list|)
block|{
name|FieldFragList
name|ffl
init|=
operator|new
name|SimpleFieldFragList
argument_list|(
name|fragCharSize
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|WeightedPhraseInfo
argument_list|>
name|wpil
init|=
operator|new
name|ArrayList
argument_list|<
name|WeightedPhraseInfo
argument_list|>
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|WeightedPhraseInfo
argument_list|>
name|ite
init|=
name|fieldPhraseList
operator|.
name|phraseList
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|WeightedPhraseInfo
name|phraseInfo
init|=
literal|null
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
operator|!
name|ite
operator|.
name|hasNext
argument_list|()
condition|)
break|break;
name|phraseInfo
operator|=
name|ite
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|phraseInfo
operator|==
literal|null
condition|)
break|break;
name|wpil
operator|.
name|add
argument_list|(
name|phraseInfo
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|wpil
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
name|ffl
operator|.
name|add
argument_list|(
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|wpil
argument_list|)
expr_stmt|;
return|return
name|ffl
return|;
block|}
block|}
end_class
end_unit
