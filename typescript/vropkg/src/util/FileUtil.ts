/*-
 * #%L
 * vropkg
 * %%
 * Copyright (C) 2023 - 2025 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 * 
 * This product is licensed to you under the BSD-2 license (the "License"). You may not use this product except in compliance with the BSD-2 License.
 * 
 * This product may include a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
 * #L%
 */
import { Stream, Readable, Writable } from 'stream';
import { EventEmitter } from 'events';

/**
 * FileUtil class manages file operations with a maximum limit of concurrent open files.
 * It provides a queuing mechanism for when the limit is reached.
 */
export class FileUtil {
    private static instance: FileUtil;
    private static readonly MAX_OPEN_FILES = 256;
    
    private openFiles: Set<Stream>;
    private waitingQueue: Array<{
        resolve: () => void;
        reject: (error: Error) => void;
    }>;
    
    private constructor() {
        this.openFiles = new Set();
        this.waitingQueue = [];
    }

    public static getInstance(): FileUtil {
        if (!FileUtil.instance) {
            FileUtil.instance = new FileUtil();
        }
        return FileUtil.instance;
    }

    /**
     * Register a new stream for tracking
     * @param stream The stream to track
     * @returns Promise that resolves when the stream can be opened
     */
    private async registerStream(stream: Stream): Promise<void> {
        if (this.openFiles.size >= FileUtil.MAX_OPEN_FILES) {
            await new Promise<void>((resolve, reject) => {
                this.waitingQueue.push({ resolve, reject });
            });
        }
        this.openFiles.add(stream);
    }

    /**
     * Unregister a stream when it's closed
     * @param stream The stream to unregister
     */
    private unregisterStream(stream: Stream): void {
        this.openFiles.delete(stream);
        if (this.waitingQueue.length > 0) {
            const next = this.waitingQueue.shift();
            if (next) {
                next.resolve();
            }
        }
    }

    /**
     * Creates a managed readable stream that is tracked for file handle limits
     * @param stream The original readable stream
     * @returns A managed readable stream
     */
    public async createManagedReadStream(stream: Readable): Promise<Readable> {
        await this.registerStream(stream);
        
        // Handle stream closure and errors
        const cleanup = () => this.unregisterStream(stream);
        stream.once('end', cleanup);
        stream.once('error', (err) => {
            cleanup();
            stream.emit('error', err);
        });
        stream.once('close', cleanup);
        
        return stream;
    }

    /**
     * Creates a managed writable stream that is tracked for file handle limits
     * @param stream The original writable stream
     * @returns A managed writable stream
     */
    public async createManagedWriteStream(stream: Writable): Promise<Writable> {
        await this.registerStream(stream);
        
        // Handle stream closure and errors
        const cleanup = () => this.unregisterStream(stream);
        stream.once('finish', cleanup);
        stream.once('error', (err) => {
            cleanup();
            stream.emit('error', err);
        });
        stream.once('close', cleanup);
        
        return stream;
    }

    /**
     * Creates a managed stream from any type of stream (Duplex, Transform, etc.)
     * @param stream The original stream
     * @returns A managed stream
     */
    public async createManagedStream<T extends Stream>(stream: T): Promise<T> {
        await this.registerStream(stream);
        
        // Handle all possible stream closure events
        const cleanup = () => this.unregisterStream(stream);
        if (stream instanceof Readable) {
            stream.once('end', cleanup);
        }
        if (stream instanceof Writable) {
            stream.once('finish', cleanup);
        }
        stream.once('error', (err) => {
            cleanup();
            stream.emit('error', err);
        });
        stream.once('close', cleanup);
        
        return stream;
    }

    /**
     * Wraps a resource to ensure proper cleanup
     * @param resource The resource to wrap
     * @returns The wrapped resource
     */
    public async createManagedResource<T extends EventEmitter>(resource: T): Promise<T> {
        await this.registerStream(resource as any);
        
        const cleanup = () => {
            this.unregisterStream(resource as any);
        };

        // Set up event listeners for cleanup
        resource.once('close', cleanup);
        resource.once('error', (err) => {
            cleanup();
            resource.emit('error', err);
        });
        
        return resource;
    }

    /**
     * Get the current number of open files
     * @returns The number of open files
     */
    public getOpenFileCount(): number {
        return this.openFiles.size;
    }

    /**
     * Get the current size of the waiting queue
     * @returns The number of operations waiting for file handles
     */
    public getWaitingQueueSize(): number {
        return this.waitingQueue.length;
    }
}
