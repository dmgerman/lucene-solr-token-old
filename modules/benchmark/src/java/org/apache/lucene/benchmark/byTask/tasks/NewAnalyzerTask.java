begin_unit
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.tasks
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
name|tasks
package|;
end_package
begin_comment
comment|/**  * Copyright 2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
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
name|benchmark
operator|.
name|byTask
operator|.
name|PerfRunData
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
name|util
operator|.
name|Version
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import
begin_comment
comment|/**  * Create a new {@link org.apache.lucene.analysis.Analyzer} and set it it in the getRunData() for use by all future tasks.  *  */
end_comment
begin_class
DECL|class|NewAnalyzerTask
specifier|public
class|class
name|NewAnalyzerTask
extends|extends
name|PerfTask
block|{
DECL|field|analyzerClassNames
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|analyzerClassNames
decl_stmt|;
DECL|field|current
specifier|private
name|int
name|current
decl_stmt|;
DECL|method|NewAnalyzerTask
specifier|public
name|NewAnalyzerTask
parameter_list|(
name|PerfRunData
name|runData
parameter_list|)
block|{
name|super
argument_list|(
name|runData
argument_list|)
expr_stmt|;
name|analyzerClassNames
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
DECL|method|createAnalyzer
specifier|public
specifier|static
specifier|final
name|Analyzer
name|createAnalyzer
parameter_list|(
name|String
name|className
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|Analyzer
argument_list|>
name|clazz
init|=
name|Class
operator|.
name|forName
argument_list|(
name|className
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|Analyzer
operator|.
name|class
argument_list|)
decl_stmt|;
try|try
block|{
comment|// first try to use a ctor with version parameter (needed for many new Analyzers that have no default one anymore
name|Constructor
argument_list|<
name|?
extends|extends
name|Analyzer
argument_list|>
name|cnstr
init|=
name|clazz
operator|.
name|getConstructor
argument_list|(
name|Version
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|cnstr
operator|.
name|newInstance
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|nsme
parameter_list|)
block|{
comment|// otherwise use default ctor
return|return
name|clazz
operator|.
name|newInstance
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|doLogic
specifier|public
name|int
name|doLogic
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|className
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|current
operator|>=
name|analyzerClassNames
operator|.
name|size
argument_list|()
condition|)
block|{
name|current
operator|=
literal|0
expr_stmt|;
block|}
name|className
operator|=
name|analyzerClassNames
operator|.
name|get
argument_list|(
name|current
operator|++
argument_list|)
expr_stmt|;
name|Analyzer
name|analyzer
init|=
literal|null
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|className
operator|||
literal|0
operator|==
name|className
operator|.
name|length
argument_list|()
condition|)
block|{
name|className
operator|=
literal|"org.apache.lucene.analysis.standard.StandardAnalyzer"
expr_stmt|;
block|}
if|if
condition|(
operator|-
literal|1
operator|==
name|className
operator|.
name|indexOf
argument_list|(
literal|"."
argument_list|)
condition|)
block|{
try|try
block|{
comment|// If no package, first attempt to instantiate a core analyzer
name|String
name|coreClassName
init|=
literal|"org.apache.lucene.analysis.core."
operator|+
name|className
decl_stmt|;
name|analyzer
operator|=
name|createAnalyzer
argument_list|(
name|coreClassName
argument_list|)
expr_stmt|;
name|className
operator|=
name|coreClassName
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
comment|// If not a core analyzer, try the base analysis package
name|className
operator|=
literal|"org.apache.lucene.analysis."
operator|+
name|className
expr_stmt|;
name|analyzer
operator|=
name|createAnalyzer
argument_list|(
name|className
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|className
operator|.
name|startsWith
argument_list|(
literal|"standard."
argument_list|)
condition|)
block|{
name|className
operator|=
literal|"org.apache.lucene.analysis."
operator|+
name|className
expr_stmt|;
block|}
name|analyzer
operator|=
name|createAnalyzer
argument_list|(
name|className
argument_list|)
expr_stmt|;
block|}
name|getRunData
argument_list|()
operator|.
name|setAnalyzer
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Changed Analyzer to: "
operator|+
name|className
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Error creating Analyzer: "
operator|+
name|className
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
literal|1
return|;
block|}
comment|/**    * Set the params (analyzerClassName only),  Comma-separate list of Analyzer class names.  If the Analyzer lives in    * org.apache.lucene.analysis, the name can be shortened by dropping the o.a.l.a part of the Fully Qualified Class Name.    *<p/>    * Example Declaration: {"NewAnalyzer" NewAnalyzer(WhitespaceAnalyzer, SimpleAnalyzer, StopAnalyzer, standard.StandardAnalyzer)>    * @param params analyzerClassName, or empty for the StandardAnalyzer    */
annotation|@
name|Override
DECL|method|setParams
specifier|public
name|void
name|setParams
parameter_list|(
name|String
name|params
parameter_list|)
block|{
name|super
operator|.
name|setParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
for|for
control|(
name|StringTokenizer
name|tokenizer
init|=
operator|new
name|StringTokenizer
argument_list|(
name|params
argument_list|,
literal|","
argument_list|)
init|;
name|tokenizer
operator|.
name|hasMoreTokens
argument_list|()
condition|;
control|)
block|{
name|String
name|s
init|=
name|tokenizer
operator|.
name|nextToken
argument_list|()
decl_stmt|;
name|analyzerClassNames
operator|.
name|add
argument_list|(
name|s
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc)    * @see org.apache.lucene.benchmark.byTask.tasks.PerfTask#supportsParams()    */
annotation|@
name|Override
DECL|method|supportsParams
specifier|public
name|boolean
name|supportsParams
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit
