
package org.spiderland.Psh;

import java.util.*;
import java.io.*;

/**
 * An abstract class for running genetic algorithms.
 */

public abstract class GA {
    protected GAIndividual _populations[][];
    protected int _currentPopulation;
    protected int _generationCount;
    
    protected float _mutationPercent;
    protected float _crossoverPercent;
    protected float _migrationPercent; // not implemented

    protected float _bestFitness;
    protected float _meanFitness;
    protected int _bestIndividual;
    
    protected ArrayList< Float > _bestErrors;
    protected int _bestSize;

    protected int _trivialGeographyRadius;
    protected int _tournamentSize;
    protected int _maxGenerations;
    
    protected Random _RNG;
    
    protected HashMap< String, String > _parameters;
    protected ArrayList< GATestCase > _testCases;
    
    protected Class<?> _individualClass;
    
    protected OutputStream _outputStream;

/**
 * Factor method for creating a GA object, with the GA class specified by the 
 * problem-class parameter.
 */

    public static GA GAWithParameters( HashMap< String, String > inParams ) throws Exception {
	Class<?> cls = Class.forName( inParams.get( "problem-class" ) );

	Object gaObject = cls.newInstance();
	
	if( ! ( gaObject instanceof GA ) )
	    throw( new Exception( "problem-class must inherit from class GA" ) );
	
	GA ga = (GA)gaObject;
	
	ga.SetParams( inParams );
	ga.InitFromParameters();
	
	return ga;
    }
    
    protected GA() {
	_RNG = new Random();
	_testCases = new ArrayList< GATestCase >();
	_bestFitness = Float.MAX_VALUE;
	_bestSize = 0;
	_outputStream = System.out;
    }
    
    /**
     * Sets the parameters dictionary for this GA run.
     */
    
    protected void SetParams( HashMap< String, String > inParams ) {
	_parameters = inParams;
    }
    
    /**
     * Utility function to fetch an non-optional string value from the parameter list.
     * @param inName 	the name of the parameter.
     */
    
    protected String GetParam( String inName ) throws Exception {
	return GetParam( inName, false );
    }
    
    /**
     * Utility function to fetch a string value from the parameter list, throwing an exception.
     *
     * @param inName 	the name of the parameter.
     * @param inOptional 	whether the parameter is optional.  If a parameter is not optional,
     * 			this method will throw an exception if the parameter is not found.
     * @return 		the string value for the parameter.
     */
    
    protected String GetParam( String inName, boolean inOptional ) throws Exception {
	String value = _parameters.get( inName );
	
	if( value == null && !inOptional ) 
	    throw new Exception( "Could not locate required parameter \"" + inName + "\"" );
	
	return value;
    }
    
    /**
     * Utility function to fetch an non-optional float value from the parameter list.
     * @param inName 	the name of the parameter.
     */
    
    protected float GetFloatParam( String inName ) throws Exception {
	return GetFloatParam( inName, false );
    }
    
    /**
     * Utility function to fetch a float value from the parameter list, throwing an exception.
     *
     * @param inName 	the name of the parameter.
     * @param inOptional 	whether the parameter is optional.  If a parameter is not optional,
     * 			this method will throw an exception if the parameter is not found.
     * @return 		the float value for the parameter.
     */
    
    protected float GetFloatParam( String inName, boolean inOptional ) throws Exception {
	String value = _parameters.get( inName );
	
	if( value == null && !inOptional ) 
	    throw new Exception( "Could not locate required parameter \"" + inName + "\"" );
	
	return Float.parseFloat( value );
    }
    
    /**
     * Sets up the GA object with the previously set parameters.  This method is 
     * typically overridden to read in custom parameters associated with custom 
     * subclasses.  Subclasses must always call the base class implementation first 
     * to ensure that all base parameters are setup.
     */
    
    protected void InitFromParameters() throws Exception {
	_individualClass	= Class.forName( GetParam( "individual-class" ) );
	
	_mutationPercent 	= GetFloatParam( "mutation-percent" );
	_crossoverPercent 	= GetFloatParam( "crossover-percent" );
	_maxGenerations		= (int)GetFloatParam( "max-generations" );
	_tournamentSize 	= (int)GetFloatParam( "tournament-size" );
	_trivialGeographyRadius	= (int)GetFloatParam( "trivial-geography-radius" );

	Resize( (int)GetFloatParam( "population-size" ) );
	
	String outputfile = GetParam( "output-file", true );
	
	if( outputfile != null )
	    _outputStream = new FileOutputStream( new File( outputfile ) );
    }
    
    
    /**
     * Sets the population size and resets the GA generation count.
     * @param inSize the size of the new GA population.
     */
    
    protected void Resize( int inSize ) throws Exception {
	_populations = new GAIndividual[ 2 ][ inSize ];
	_currentPopulation = 0;
	_generationCount = 0;
	
	Object iObject = _individualClass.newInstance();
	
	if( !( iObject instanceof GAIndividual ) ) 
	    throw new Exception( "individual-class must inherit from class GAIndividual" );
	
	GAIndividual individual = (GAIndividual)iObject;
	
	for( int i = 0; i < 2; i++ ) {
	    for( int j = 0; j < inSize; j++ ) {
		_populations[ i ][ j ] = individual.clone();
		InitIndividual( _populations[ i ][ j ] );
	    }
	}
    }
    
    /**
     * Run the main GP run loop until the generation limit it met.
     * @return 	true, indicating the the execution of the GA is complete.
     */
    
    public boolean Run() throws Exception {
	return Run( -1 );
    }
    
    /**
     * Run the main GP run loop until the generation limit it met, or until
     * the provided number of generations has elapsed.
     * 
     * @param inGenerations The maximum number of generations to run during this call.  
     * 			This is distinct from the hard generation limit which determines
     * 			when the GA is actually complete.
     * @return true if the the execution of the GA is complete.
     */
    
    public boolean Run( int inGenerations ) throws Exception {
	while( !Terminate() && inGenerations != 0 ) {
	    BeginGeneration();
	    
	    Evaluate();
	    Reproduce();
	    
	    EndGeneration();
	    
	    Print( Report() );
	    
	    if(!Terminate() && inGenerations != 0 ){
		_currentPopulation = ( _currentPopulation == 0 ? 1 : 0 );
		_generationCount++;
		inGenerations--;
	    }
	    
	    System.gc();
	}

	Print(FinalReport());
	
	return ( _generationCount < _maxGenerations );
    }
    
    /**
     * Determine whether the GA should terminate.
     * This method may be overridden by subclasses to customize GA behavior.
     */
    
    protected boolean Terminate() {
	return ( _generationCount >= _maxGenerations || Success() );
    }
    
    /**
     * Determine whether the GA has succeeded.
     * This method may be overridden by subclasses to customize GA behavior.
     */
    
    protected boolean Success() {
	return _bestFitness == 0.0;
    }
    
    /**
     * Evaluates the current population and updates their fitness values.
     * This method may be overridden by subclasses to customize GA behavior.
     */
    
    protected void Evaluate() {
	float totalFitness	= 0;
	_bestFitness 		= Float.MAX_VALUE;
	
	for( int n = 0; n < _populations[ _currentPopulation ].length; n++ ) {
	    GAIndividual i = _populations[ _currentPopulation ][ n ];
	    
	    EvaluateIndividual( i );
	    
	    totalFitness += i.GetFitness();
	    
	    if( i.GetFitness() < _bestFitness ) {
		_bestFitness = i.GetFitness();
		_bestIndividual = n;
		_bestSize = ((PushGPIndividual)i)._program.programsize();
		_bestErrors = i.GetErrors();
	    }
	}
	
	_meanFitness = totalFitness / _populations[ _currentPopulation ].length;
    }
    
    /**
     * Reproduces the current population into the next population slot.
     * This method may be overridden by subclasses to customize GA behavior.
     */
    
    protected void Reproduce() {
	int nextPopulation = _currentPopulation == 0 ? 1 : 0;
	
	for( int n = 0; n < _populations[ _currentPopulation ].length; n++ ) {
	    float method = _RNG.nextInt( 100 );
	    GAIndividual next;
	    
	    if( method < _mutationPercent ) {
		next = ReproduceByMutation( n );
	    } else if( method < _crossoverPercent + _mutationPercent ) {
		next = ReproduceByCrossover( n );
	    } else {
		next = ReproduceByClone( n );
	    }
	    
	    _populations[ nextPopulation ][ n ] = next;
	}
    }
    
    /**
     * Prints out population report statistics.  
     * This method may be overridden by subclasses to customize GA behavior.
     */
    
    protected String Report() {
	String report = "\n";
	report += ";;--------------------------------------------------------;;\n";
	report += ";;---------------";
	report += " Report for Generation " + _generationCount + " ";
	report += "---------------;;\n";
	report += ";;--------------------------------------------------------;;\n";
	report += ";; Best Program:\n  " + _populations[ _currentPopulation ][ _bestIndividual ] + "\n\n";

	report += ";; Best Program Fitness: " + _bestFitness + "\n";
	report += ";; Best Program Errors: (";
	for(int i = 0; i < _testCases.size(); i++){
	    if(i != 0)
		report += " ";
	    report += "(" + _testCases.get(i)._input + " ";
	    report += Math.abs(_bestErrors.get(i)) + ")";
	}
	report += ")\n";
	report += ";; Best Program Size: " + _bestSize + "\n\n";
	
	String mem = String.valueOf( Runtime.getRuntime().totalMemory() / 10000000.0f );
	report += ";; Memory usage: " + mem + "\n";
	report += ";; Mean Fitness: " + _meanFitness + "\n";
	
	return report;
    }

    protected String FinalReport(){
	boolean success = Success();
	String report = "\n";

	report += "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n";
	report += "                        ";

	if(success){
	    report += "Success";
	}
	else{
	    report += "Failure";
	}
	report += " at Generation " + _generationCount + "\n";
	report += "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n";

	report += ">> Best Program: " + _populations[ _currentPopulation ][ _bestIndividual ] + "\n";
	report += ">> Fitness: " + _bestFitness + "\n";
	report += ">> Errors: (";
	for(int i = 0; i < _testCases.size(); i++){
	    if(i != 0)
		report += " ";
	    report += "(" + _testCases.get(i)._input + " ";
	    report += Math.abs(_bestErrors.get(i)) + ")";
	}
	report += ")\n";
	report += ">> Size: " + _bestSize + "\n";

	return report;
    }
    
    /**
     * Logs output of the GA run to the appropriate location (which may be stdout, or a file).
     */
    
    protected void Print( String inStr ) throws Exception {
	if( _outputStream != null )
	    _outputStream.write( inStr.getBytes() );	
    }
    
    /**
     * Preforms a tournament selection, return the best individual from a sample of the given size.
     *
     * @param inSize	The number of individuals to consider in the tournament selection.
     */
    
    protected GAIndividual TournamentSelect( int inSize, int inIndex ) {
	int popsize = _populations[ _currentPopulation ].length;

	int best = TournamentSelectionIndex(inIndex, popsize);
	float bestFitness = _populations[ _currentPopulation ][ best ].GetFitness();
	
	for( int n = 0; n < inSize - 1; n++ ) {
	    int candidate = TournamentSelectionIndex(inIndex, popsize);
	    float candidateFitness = _populations[ _currentPopulation ][ candidate ].GetFitness();
	    
	    if( candidateFitness < bestFitness ) {
		best = candidate;
		bestFitness = candidateFitness;
	    }
	}

	return _populations[ _currentPopulation ][ best ];
    }
    
    /** 
     * Produces an index for a tournament selection candidate.
     * @param inIndex	The index which is to be replaced by the current reproduction event (used only if trivial-geography is enabled).
     * @param inPopsize	The size of the population.
     * @return the index for the tournament selection.
     */
    
    protected int TournamentSelectionIndex( int inIndex, int inPopsize ) {
	if( _trivialGeographyRadius != 0 ) {
	    int index = ( _RNG.nextInt( _trivialGeographyRadius * 2 ) - _trivialGeographyRadius ) + inIndex;
	    if(index < 0)
		index += inPopsize;

	    return ( index % inPopsize );
	}
	else {
	    return _RNG.nextInt( inPopsize );
	}
    }
    
    /**
     * Clones an individual selected through tournament selection.
     * @return the cloned individual.
     */
    
    protected GAIndividual ReproduceByClone( int inIndex ) {
	return TournamentSelect( _tournamentSize, inIndex ).clone();
    }
    
    /**
     * Computes the absolute-sum-of-errors fitness from an error vector.
     * @return the total error value for the vector.
     */
    
    protected float AbsoluteSumOfErrors( ArrayList< Float > inArray ) {
	float total = 0.0f;
	
	for( int n = 0; n < inArray.size(); n++ )
	    total += Math.abs( inArray.get( n ) );
	
	return total;
    }
    
    
    protected void BeginGeneration() {};
    protected void EndGeneration() {};
    
    abstract protected void InitIndividual( GAIndividual inIndividual );
    
    abstract protected int EvaluateIndividual( GAIndividual inIndividual );
    abstract protected float EvaluateTestCase( GAIndividual inIndividual, Object inInput, Object inOutput );
    
    abstract protected GAIndividual ReproduceByCrossover( int inIndex );
    abstract protected GAIndividual ReproduceByMutation( int inIndex );
    abstract protected GAIndividual ReproduceBySimplification( int inIndex );
    
}